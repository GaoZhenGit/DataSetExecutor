# -*- coding: UTF-8 -*-
from __future__ import division
import time
import sys

# baseDir = './../../../../'
baseDir = './'
test_set_dir = baseDir + 'dataDir/testset.txt'
conculsionDir = baseDir + 'conculsionDir/'

#根据命令行参数来决定对哪个文件进行conclusion
if len(sys.argv) == 1:
    result_set_dir = baseDir + 'mfDir/score/score_edge.txt'
elif sys.argv[1] == 'dirmf':
    result_set_dir = baseDir + 'mfDir/score/s_dir_mf_edge.txt'
elif sys.argv[1] == 'mosttop':
    result_set_dir = baseDir + 'mfDir/score/s_most_top_edge.txt'
elif sys.argv[1] == 'dirlda':
    result_set_dir = baseDir + 'mfDir/score/s_dir_lda_edge.txt'

output = open(conculsionDir + str(int(time.time())) + '.txt','w')

test_set = []
with open(test_set_dir) as file:
    for line in file:
        test_set.append(line.strip())
print 'test set size:', len(test_set)
output.write('test set size:' + str(len(test_set)) + '\n')

result_set = []
with open(result_set_dir) as file:
    for line in file:
        result_set.append(line.strip())
print 'result set size:', len(result_set)
output.write('result set size:' + str(len(result_set)) + '\n')

intersection = list(set(test_set).intersection(set(result_set)))
print 'intersection set size:', len(intersection)
output.write('intersection set size:' + str(len(intersection)) + '\n')

recall = len(intersection) / len(test_set)
precision = len(intersection) / len(result_set)
f1 = 2 * precision * recall / (precision + recall)

#计算转化率
data_info = open(baseDir + 'ldaDir/model-final.others')
for line in data_info:
    if line.find('ndocs') >= 0:
        doc_count = int(line.split('=')[1])
    if line.find('nwords') >= 0:
        word_count = int(line.split('=')[1])

left_list = []
for line in intersection:
    left_id,right_id = line.split(' ')
    left_list.append(left_id)
right_list = []
for line in intersection:
    left_id,right_id = line.split(' ')
    right_list.append(right_id)

conversion1 = len(set(left_list)) / doc_count
conversion2 = len(set(right_list)) / word_count

print 'recall:', recall * 100, '%'
output.write('recall:' + str(recall * 100) + '%\n')
print 'precision:', precision * 100, '%'
output.write('precision:' + str(precision * 100) + '%\n')
print 'f1:', f1
output.write('f1:' + str(f1) + '\n')
print 'conversion1:',conversion1,len(set(left_list))
print 'conversion2:',conversion2,len(set(right_list))
output.write('conversion1: ' + str(conversion1) + '\n')
output.write('conversion2: ' + str(conversion2) + '\n')
output.close()