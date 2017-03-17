# -*- coding: UTF-8 -*-
import scipy.sparse as sparse
import sys
sys.path.append("./src")
import com.company.mf.ifmf
import gc

baseDir = './'
threadHold = 15

print('start running mf without lda')
# 获取矩阵信息
data_info = open(baseDir + 'ldaDir/model-final.others')
for line in data_info:
    if line.find('ndocs') >= 0:
        user_num = int(line.split('=')[1])
    if line.find('nwords') >= 0:
        item_num = int(line.split('=')[1])

matrix = sparse.dok_matrix((user_num, item_num), dtype=int)

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

with open(baseDir + 'ldaDir/database-result-unnamed.txt') as file:
    file.readline()
    for i, line in enumerate(file):
        words = line.strip().split(' ')
        for word in words:
            j = word_list.index(word)
            matrix[i, j] = 1

print 'read matrix finish'
P, Q = com.company.mf.ifmf.alternating_least_squares_cg(matrix, 10, regularization=0.01, iterations=2)
print 'mf finish'
Q = Q.T
score = P.dot(Q)
del P,Q
gc.collect()
print 'mutilply finish'
output = open(baseDir + 'mfDir/score/s_dir_mf.txt','w')
edge = open(baseDir + 'mfDir/score/s_dir_mf_edge.txt', 'w')
for i in xrange(score.shape[0]):
    list = score[i].tolist()
    clist = sorted(list, reverse=True)[0:threadHold]
    doc_id = doc_list[i]
    for j in clist:
        output.write(str(list.index(j)) + ":" + str(j))
        output.write(' ')

        word_id = word_list[list.index(j)]
        edge.write(doc_id + ' ' + word_id + '\n')
    output.write('\n')
    del list
    del clist
output.close()
del score
gc.collect()
