# -*- coding: UTF-8 -*-
import MySQLdb

testset_rate = 0.1
testset_mod = int(1 / testset_rate)
# baseDir = './../../../../'
baseDir = './'
outputDir = baseDir + 'dataDir/'

db = MySQLdb.connect("localhost", "root", "123456", "twitter")
cursor = db.cursor()

# sql = 'select `srcid`, `desid` from `chinese_relation` order by `srcid`'
# sql = 'select `desid`, `srcid` from `chinese_relation` order by `desid`'
sql = 'select `f`,`g` from `filter10_5` order by `f`'
# sql = 'select `g`,`f` from `filter10_5` order by `g`'

cursor.execute(sql)

query_result = cursor.fetchall()
last_id = 0L
testset_file = open(outputDir + 'testset.txt', 'w')
named_data_file = open(outputDir + 'database-result-named.txt', 'w')
unnamed_data_file = open(outputDir + 'database-result-unnamed.txt', 'w')
sorted_data_file = open(outputDir + 'database-result-sort.txt', 'w')
# for i, result_set in enumerate(query_result):
#     if i % testset_mod == 0:
#         testset_file.write(str(result_set[0]) + ' ' + str(result_set[1]) + '\n')
#     else:
#         if result_set[0] == last_id:
#             named_data_file.write(str(result_set[1]) + ' ')
#             unnamed_data_file.write(str(result_set[1]) + ' ')
#         else:
#             named_data_file.write('\n')
#             unnamed_data_file.write('\n')
#             sorted_data_file.write('\n')
#
#             named_data_file.write(str(result_set[0]) + ':')
#             sorted_data_file.write(str(result_set[0]))
#
#             named_data_file.write(str(result_set[1]) + ' ')
#             unnamed_data_file.write(str(result_set[1]) + ' ')
#         last_id = result_set[0]
query_map = {}
query_follower_list = []  # 用于标记follower顺序（dict没有顺序）
for i, result_set in enumerate(query_result):
    follower = str(result_set[0])
    followee = str(result_set[1])
    if query_map.has_key(follower):
        query_map[follower].append(followee)
    else:
        query_follower_list.append(follower)
        query_map[follower] = []
        query_map[follower].append(followee)
for f in query_follower_list:
    followee_list = query_map[f]
    has_write_f = False
    for gi, g in enumerate(followee_list):
        if gi % testset_mod == 1:
            testset_file.write(f + ' ' + g + '\n')
        else:
            if has_write_f:
                named_data_file.write(g + ' ')
                unnamed_data_file.write(g + ' ')
            else:
                named_data_file.write('\n' + f + ':')
                named_data_file.write(g + ' ')
                unnamed_data_file.write('\n' + g + ' ')
                sorted_data_file.write('\n' + f)
                has_write_f = True
testset_file.close()
named_data_file.close()
unnamed_data_file.close()
sorted_data_file.close()
db.close()
