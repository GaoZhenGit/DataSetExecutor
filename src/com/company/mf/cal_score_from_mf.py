# -*- coding: UTF-8 -*-
import scipy.sparse as sparse
import sys
import gc

# mfDir = './../../../../mfDir/'
mfDir = './mfDir/'
matrixDir = mfDir + 'matrix/'
pqDir = mfDir + 'PQ/'
scoreDir = mfDir + 'score/'
topicCount = 15
factorsCount = 10
threadHold = 64


def compute_score(P, Pshape, Q, Qshape, scoreFile):
    Pmatrix = sparse.dok_matrix(Pshape, dtype=float)
    with open(P) as file:
        for row, fileLine in enumerate(file):
            lines = fileLine.strip().split(' ')
            for col, element in enumerate(lines):
                Pmatrix[row, col] = float(element)

    Qmatrix = sparse.dok_matrix(Qshape, dtype=float)
    with open(Q) as file:
        for row, fileLine in enumerate(file):
            lines = fileLine.strip().split(' ')
            for col, element in enumerate(lines):
                Qmatrix[row, col] = float(element)

    print('size of p:' + str(sys.getsizeof(Pmatrix)))
    print('size of q:' + str(sys.getsizeof(Qmatrix)))
    score = Pmatrix.dot(Qmatrix)
    del Pmatrix, Qmatrix
    gc.collect()
    print(score.shape)

    output = open(scoreFile,'w')
    for i in xrange(score.shape[0]):
        list = (score[i].toarray())[0].tolist()
        clist = sorted(list, reverse=True)[0:threadHold]
        for j in clist:
            output.write(str(list.index(j)) + ":" + str(j))
            output.write(' ')
        output.write('\n')
        del list
        del clist
    output.close()
    del score
    gc.collect()

    #下面模拟矩阵相乘，由于需要耗费大量内存，所以针对结果矩阵排序处理后直接输出
    # finalRow = pShape[0]
    # finalCol = qShape[1]

    # output = open(scoreFile,'w')
    # for i in xrange(finalRow):
    #     print '.' ,
    #     plist = Pmatrix.getrow(i)#左矩阵获取的其中一行
    #     scoreList = []
    #     for j in xrange(finalCol):#针对右矩阵每一列和左矩阵点乘
    #         qlist = Qmatrix.getcol(j)
    #         score = plist.dot(qlist)[0,0]
    #         scoreList.append(score)

    #     clist = sorted(scoreList,reverse = True)[0:threadHold]#取排序前n个
    #     for sc in clist:
    #         output.write(str(scoreList.index(sc)))
    #         output.write(':')
    #         output.write(str(sc))
    #         output.write(' ')
    #     output.write('\n')


def getShape(matrixFile):
    with open(matrixFile) as file:
        list = file.readline().strip().split('*')
    list[0] = int(list[0])
    list[1] = int(list[1])
    return list


for i in xrange(topicCount):
    pFile = pqDir + str(i) + '_P'
    qFile = pqDir + str(i) + '_Q'
    pShape = (getShape(matrixDir + 'zMsimple' + str(i))[0], factorsCount)
    qShape = (factorsCount, getShape(matrixDir + 'zMsimple' + str(i))[1])
    compute_score(pFile, pShape, qFile, qShape, scoreDir +'s' + str(i))
    print(str(i) + 'compute finish')

    # x =[4, 6, 2, 1, 7, 9]
    # y = sorted(x,reverse = True)[0:3]
    # print(x)
    # print(y)
    # print(x.index(9))
