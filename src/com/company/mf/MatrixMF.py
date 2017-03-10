import scipy.sparse as sparse
import ifmf

mfDir = './../../../../mfDir/'
inputDir = mfDir + 'matrix/'
outputDir = mfDir + 'PQ/'
topicCount = 15


def doMatrixF (inputFile, outputFilePreFix):
    with open(inputFile) as file:
        for row, line in enumerate(file):
            if row == 0:
                num_users, num_items = line.strip().split('*')
                num_users = int(num_users)
                num_items = int(num_items)
                matrix = sparse.dok_matrix((num_users, num_items), dtype=int)
            else :
                follower = line.strip().split(' ')
                if len(follower) > 1 :
                    for col in follower:
                        matrix[int(row - 1),int(col)] = 1

    P, Q = ifmf.alternating_least_squares_cg(matrix, 10,regularization=0.01, iterations=15)

    print('start outputing P:')
    Pfile = open(outputFilePreFix + "P",'w')
    xi,xj = P.shape
    for i in xrange(xi) :
        for j in xrange(xj) :
            print_value = P[i,j]
            if print_value >= 0:
                Pfile.write("%.15f"%print_value)
            else:
                Pfile.write("%.14f"%print_value)
            Pfile.write(' ')
        Pfile.write('\n')
    Pfile.close()

    print('start outputing Q:')
    Qfile = open(outputFilePreFix + "Q",'w')
    Q = Q.T
    yi,yj = Q.shape
    for i in xrange(yi) :
        for j in xrange(yj) :
            print_value = Q[i,j]
            if print_value >= 0:
                Qfile.write("%.15f"%print_value)
            else:
                Qfile.write("%.14f"%print_value)
            Qfile.write(' ')
        Qfile.write('\n')
    Qfile.close()

for index in xrange(topicCount):
    curInputFile = inputDir + 'zMsimple' + str(index)
    curOutputFile = outputDir + str(index) + '_'
    print(str(index) + ' matrix start input:' + curInputFile + ' output:' + curOutputFile)
    doMatrixF(curInputFile, curOutputFile)
