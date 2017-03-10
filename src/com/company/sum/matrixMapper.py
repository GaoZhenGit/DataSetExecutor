# -*- coding: UTF-8 -*-
import scipy.sparse as sparse
import sys
import gc
#doc==follower, word==followee
# baseDir = './../../../../'
baseDir = './'
doc_dir = baseDir + 'ldaDir/database-result-sort.txt'
word_dir = baseDir + 'ldaDir/wordmap.txt'

class MatrixMapper:
    def __init__(self):
        print ('matrix mapper init')
        self.__read()

    def __read(self):
        doc_file = open(doc_dir,'r')
        self.doc_list = []
        for line in doc_file:
            self.doc_list.append(line.strip())
        doc_file.close()
        del doc_file

        word_file = open(word_dir,'r')
        self.word_list = range(int(word_file.readline()))
        for line in word_file:
            word_id,word_index = line.strip().split(' ')
            self.word_list[int(word_index)] = word_id
        word_file.close()
        del word_file

    def reset(self,fcn_list,gcn_list):
        self.fcn_list = fcn_list
        self.gcn_list = gcn_list

    def mapI(self,originI):
        doc_id = self.fcn_list[originI]
        return self.doc_list.index(doc_id)

    def mapJ(self,originJ):
        word_id = self.gcn_list[originJ]
        return self.word_list.index(word_id)

    def get_real_doc_id(self,i):
        return self.doc_list[i]

    def get_real_word_id(self,j):
        return self.word_list[j]