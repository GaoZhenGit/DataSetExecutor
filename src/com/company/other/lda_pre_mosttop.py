# -*- coding: UTF-8 -*-
import os

baseDir = './'
dataDir = baseDir + 'ldaDir/database-result-unnamed.txt'
sortDir = baseDir + 'ldaDir/database-result-sort.txt'

word_map = {}
with open(dataDir) as file:
    file.readline()
    # 统计每个word（被关注者）出现次数
    for line in file:
        line_words = line.strip().split(' ')
        for word in line_words:
            if word_map.has_key(word):
                word_map[word] += 1
            else:
                word_map[word] = 1

def sort_by_value(d):
    items = d.items()
    backitems = [[v[1], v[0]] for v in items]
    backitems.sort(reverse=True)
    return [backitems[i][1] for i in range(0, len(backitems))]

word_list = sort_by_value(word_map)[0:15]
print word_list

output_file_mem = []
with open(dataDir) as file:
    output_file_mem.append(file.readline()) # 保留第一行
    for line in file:
        line = line.strip() + ' '
        for it in word_list:
            line += it + ' '
        line += '\n'
        output_file_mem.append(line)
with open(baseDir + 'ldaDir/test.txt','w') as file:
    for line in output_file_mem:
        file.write(line)

os.remove(dataDir)
os.rename(baseDir + 'ldaDir/test.txt', dataDir)