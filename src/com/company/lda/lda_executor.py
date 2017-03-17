# -*- coding: UTF-8 -*-
import numpy as numpy
import scipy.sparse as sparse
import lda

baseDir = './'
ldaDir = baseDir + 'ldaDir/'
sort_dir = ldaDir + 'database-result-sort.txt'
unnamed_dir = ldaDir + 'database-result-unnamed.txt'

word_map_dir = ldaDir + 'wordmap.txt'
model_name = 'model-final'
theta_dir = ldaDir + model_name + '.theta'
phi_dir = ldaDir + model_name + '.phi'
other_dir = ldaDir + model_name + '.others'

topic_count = 15
alpha = 0.5


def readData():
    word_map = {}
    matrix_data = []
    with open(unnamed_dir) as file:
        file.readline()
        count = 0
        # 统计每个word（被关注者）出现顺序,以及录入data表
        for line in file:
            line_words = line.strip().split(' ')
            matrix_data.append(line_words)
            for word in line_words:
                if word_map.has_key(word):
                    pass
                else:
                    word_map[word] = count
                    count += 1
    # 输出wordmap
    with open(word_map_dir, 'w') as file:
        file.write(str(len(word_map)) + '\n')
        for it in word_map.items():
            file.write(it[0])
            file.write(' ')
            file.write(str(it[1]))
            file.write('\n')

    print len(matrix_data), len(word_map)
    return matrix_data, word_map


def dataToMatrix(matrix_data, word_map):
    matrix = sparse.dok_matrix((len(matrix_data), len(word_map)), dtype=int)
    for i, i_t in enumerate(matrix_data):
        for j_t in i_t:
            matrix[i, word_map[j_t]] = 1
    return matrix


def print_model(model, matrix):
    # 输出others文件
    with open(other_dir, 'w') as file:
        file.write('alpha=' + str(alpha) + '\n')
        file.write('ntopics=' + str(topic_count) + '\n')
        file.write('ndocs=' + str(matrix.shape[0]) + '\n')
        file.write('nwords=' + str(matrix.shape[1]) + '\n')
    # 输出theta
    theta = model.doc_topic_
    with open(theta_dir, 'w') as file:
        for i in theta:
            for j in i:
                file.write(str(j) + ' ')
            file.write('\n')

    # 输出phi
    phi = model.topic_word_
    with open(phi_dir, 'w') as file:
        for i in phi:
            for j in i:
                file.write(str(j) + ' ')
            file.write('\n')


matrix_data, word_map = readData()
X = dataToMatrix(matrix_data, word_map)
print 'load matrix finish'

model = lda.LDA(n_topics=topic_count, random_state=None, n_iter=100, alpha=alpha)
model.fit(X)
print 'lda finish'
print_model(model, X)
