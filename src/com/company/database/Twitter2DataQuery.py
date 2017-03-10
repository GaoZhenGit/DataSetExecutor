# -*- coding: UTF-8 -*-
import MySQLdb

testset_rate = 0.1
testset_mod = int(1 / testset_rate)
baseDir = './../../../../'
outputDir = baseDir + 'dataDir/'

db = MySQLdb.connect("localhost", "root", "123456", "twitter2")
cursor = db.cursor()

sql = 'select `srcid`, `desid` from `chinese_relation` order by `srcid`'
# sql = 'select `desid`, `srcid` from `chinese_relation` order by `desid`'


cursor.execute(sql)

query_result = cursor.fetchall()
last_id = 0L
testset_file = open(outputDir + 'testset.txt', 'w')
named_data_file = open(outputDir + 'database-result-named.txt', 'w')
unnamed_data_file = open(outputDir + 'database-result-unnamed.txt', 'w')
sorted_data_file = open(outputDir + 'database-result-sort.txt', 'w')
for i, result_set in enumerate(query_result):
    if i % testset_mod == 0:
        testset_file.write(str(result_set[0]) + ' ' + str(result_set[1]) + '\n')
    else:
        if result_set[0] == last_id:
            named_data_file.write(str(result_set[1]) + ' ')
            unnamed_data_file.write(str(result_set[1]) + ' ')
        else:
            named_data_file.write('\n')
            unnamed_data_file.write('\n')
            sorted_data_file.write('\n')

            named_data_file.write(str(result_set[0]) + ':')
            sorted_data_file.write(str(result_set[0]))

            named_data_file.write(str(result_set[1]) + ' ')
            unnamed_data_file.write(str(result_set[1]) + ' ')
        last_id = result_set[0]
testset_file.close()
named_data_file.close()
unnamed_data_file.close()
sorted_data_file.close()
db.close()
