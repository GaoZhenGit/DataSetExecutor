# -*- coding: UTF-8 -*-

baseDir = './'
dataDir = baseDir + 'ldaDir/database-result-unnamed.txt'
sortDir = baseDir + 'ldaDir/database-result-sort.txt'
resultDir = baseDir + 'mfDir/score/s_most_top_edge.txt'

recommend_count = 15
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

print 'words count:', len(word_map)


def sort_by_value(d):
    items = d.items()
    backitems = [[v[1], v[0]] for v in items]
    backitems.sort(reverse=True)
    return [backitems[i][1] for i in range(0, len(backitems))]

word_list = sort_by_value(word_map)[0:recommend_count]

with open(sortDir) as file:
    doc_list = [line.strip() for line in file]

# 输出edge
output = open(resultDir, 'w')
for it in doc_list:
    for w in word_list:
        output.write(it + ' ' + w + '\n')

output.close()

