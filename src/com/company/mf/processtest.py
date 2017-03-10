from multiprocessing import Pool
import multiprocessing
from time import sleep
import numpy as np


# def f(lock,x,y):
#     # lock.acquire()
#     print '|-----start' + str(x) + str(y) + '-----|'
#     # lock.release()
#     sleep(x + y)
#     # lock.acquire()
#     print '|-----end' + str(x) + str(y) + '-----|'
#     # lock.release()
#
#
# def main():
#     pool = Pool(processes=3)
#     lock = multiprocessing.Lock()
#     for i in range(5):
#         result = pool.apply_async(f, (lock,i,i*2,))
#     pool.close()
#     pool.join()
#     if result.successful():
#         print 'successful'
#
#
# if __name__ == "__main__":
#     main()

X = np.random.rand(5, 3) * 0.01
print X