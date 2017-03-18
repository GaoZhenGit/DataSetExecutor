# -*- coding: UTF-8 -*-
import scipy.sparse as sparse
import gc

baseDir = './'
theta_dir = baseDir + 'ldaDir/model-final.theta'
phi_dir = baseDir + 'ldaDir/model-final.phi'

threadHold = 15


def readLdaResult(user_num, item_num, topic_num):
    word_file = open(baseDir + '/ldaDir/wordmap.txt', 'r')
    word_list = range(int(word_file.readline()))
    for line in word_file:
        word_id, word_index = line.strip().split(' ')
        word_list[int(word_index)] = word_id
    word_file.close()
    del word_file

    doc_file = open(baseDir + 'ldaDir/database-result-sort.txt')
    doc_list = []
    for line in doc_file:
        doc_list.append(line.strip())
    doc_file.close()
    del doc_file

    theta = sparse.dok_matrix((user_num, topic_num), dtype=float)
    with open(theta_dir) as file:
        for i, line in enumerate(file):
            items = line.strip().split(' ')
            for j, it in enumerate(items):
                theta[i, j] = float(it)

    phi = sparse.dok_matrix((topic_num, item_num), dtype=float)
    with open(phi_dir) as file:
        for i, line in enumerate(file):
            items = line.strip().split(' ')
            for j, it in enumerate(items):
                phi[i, j] = float(it)

    print 'read lda finish'
    score = theta.dot(phi)
    print 'multiply finish'
    del theta, phi
    gc.collect()

    edge = open(baseDir + 'mfDir/score/s_dir_lda_edge.txt', 'w')
    for i in xrange(score.shape[0]):
        list = (score[i].toarray())[0].tolist()
        clist = sorted(list, reverse=True)[0:threadHold]
        doc_id = doc_list[i]
        for j in clist:
            word_id = word_list[list.index(j)]
            edge.write(doc_id + ' ' + word_id + '\n')
        del list
        del clist
    edge.close()

# 获取矩阵信息
with open(baseDir + 'ldaDir/model-final.others') as data_info:
    for line in data_info:
        if line.find('ndocs') >= 0:
            user_num = int(line.split('=')[1])
        if line.find('nwords') >= 0:
            item_num = int(line.split('=')[1])
        if line.find('ntopics') >= 0:
            topic_num = int(line.split('=')[1])

readLdaResult(user_num, item_num, topic_num)
