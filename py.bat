@echo off
if %1 == database python ./src/com/company/database/TwitterDataQuery.py
if %1 == mf python ./src/com/company/mf/MatrixMF.py
if %1 == cmf python ./src/com/company/mf/cal_score_from_mf.py
if %1 == sum python ./src/com/company/sum/scoreSum.py
if %1 == con python ./src/com/company/conclusion/conclusion_cal.py %2
if %1 == dirmf python ./src/com/company/other/mf_without_lda.py
if %1 == mosttop python ./src/com/company/other/most_top.py