aiolos-client
=====

####简介

aiolos-client是aiolos提供给客户端应用读取zk基础配置的的包，zk设计中加入了timestamp来判断zk的值和内存中的值是否一致，每5分钟同步一次。
服务端需要注意在设置配置的值之前先要设置timestamp的值，不然会造成客户端配置无法有效更新

门面操作类是Aiolos

