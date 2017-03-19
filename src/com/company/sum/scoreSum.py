# -*- coding: UTF-8 -*-
import scipy.sparse as sparse
import sys
import gc
import matrixMapper as Mp
import time

# baseDir = './../../../../'
baseDir = './'
lda_result_dir = baseDir + 'ldaDir/topic/'
score_dir = baseDir + 'mfDir/score/'


data_info = open(baseDir + 'ldaDir/model-final.others')
for line in data_info:
    if line.find('ndocs') >= 0:
        user_num = int(line.split('=')[1])
    if line.find('nwords') >= 0:
        item_num = int(line.split('=')[1])
    if line.find('ntopics') >= 0:
        topicCount = int(line.split('=')[1])

recommend_count = 15


def makeMatrix():
    matrix = sparse.dok_matrix((user_num, item_num), dtype=float)
    return matrix


def addScore(index, matrix, mapper):
    # f_c_n文件读入
    f_c_n_file = open(lda_result_dir + 'f_c_' + str(index), 'r')
    fcnMap = {}  # user_id到概率P的映射
    fcnList = []  # 下标的
    for line in f_c_n_file:
        user_id, userP = line.strip().split(' ')
        fcnMap[user_id] = float(userP)
        fcnList.append(user_id)
        del user_id
        del userP
    f_c_n_file.close()
    del f_c_n_file

    # g_c_n文件读入
    g_c_n_file = open(lda_result_dir + 'g_c_' + str(index), 'r')
    gcnList = []
    for line in g_c_n_file:
        user_id = line.strip()
        gcnList.append(user_id)
        del user_id
    g_c_n_file.close()
    del g_c_n_file

    mapper.reset(fcnList, gcnList)

    # s文件读入,并直接加入最终矩阵
    s_file = open(score_dir + 's' + str(index))
    for i, line in enumerate(s_file):
        real_i = mapper.mapI(i)
        userP = float(fcnMap[fcnList[i]])
        cur_user_score_list = line.strip().split(' ')
        for score_pair in cur_user_score_list:
            j, score = score_pair.split(':')
            real_j = mapper.mapJ(int(j))
            score = float(score)
            if (score != 0):
                matrix[real_i, real_j] += score * userP

    s_file.close()
    del s_file


def printMatrix(matrix, mapper):
    print('start print matrix')
    t0 = time.time()
    score_output = open(score_dir + 'score_sum.txt', 'w')
    score_edge = open(score_dir + 'score_edge.txt', 'w')
    row = matrix.shape[0]
    for i in xrange(row):
        list = matrix[i].toarray()[0].tolist()
        clist = sorted(list, reverse=True)[0:recommend_count]
        row_id = mapper.get_real_doc_id(i)
        for j in xrange(len(clist)):
            word_index = list.index(clist[j])
            word_id = mapper.get_real_word_id(word_index)
            word_score = clist[j]
            score_output.write(word_id + ':' + str(word_score) + ' ')
            score_edge.write(row_id + ' ' + word_id + '\n')
        score_output.write('\n')
    t1 = time.time()
    print(str(t1 - t0) + " cost")


result_holder = makeMatrix()
mapper = Mp.MatrixMapper()
for i in xrange(topicCount):
    print(str(i) + 'start')
    addScore(i, result_holder, mapper)
printMatrix(result_holder, mapper)
