# Asynchronous Multi-Task Learning 

Abstract—Many real-world machine learning applications involve many learning tasks that are inter-related. For example, in the healthcare domain, we need to learn a predictive model of a certain disease for many hospitals. The models for each hospital can be different because of the inherent differences in the distributions of the patient populations. However, the models are also closely related because of the nature of the learning tasks namely modeling the same disease. By simultaneously learning all the tasks, the multi-task learning (MTL) paradigm performs inductive knowledge transfer among tasks to improve the generalization performance of all tasks involved. When datasets for the learning tasks are stored in different locations, it may not always be feasible to move the data to provide a data-centralized computing environment, due to various practical issues such as high data volume and data privacy. This has posed a huge challenge to existing MTL algorithms. In this paper, we propose a principled MTL framework for distributed and asynchronous optimization. In our framework, the gradient update does not depend on and hence does not require waiting for the gradient information to be collected from all the tasks, making it especially attractive when the communication delay is too high for some task nodes. We show that many regularized MTL formulations can benefit from this framework, including the low-rank MTL for shared subspace learning. Empirical studies on both synthetic and real-world datasets demonstrate the efficiency and effectiveness of the proposed framework.


# How to Use the Java Code

There are two main java files which are Client_Square_Loss.java and Server_ProxTrace.java. Server_ProxTrace should be run at the central node end and t should read possible client IP addresses from a text file. Client_Square_Loss should be run at the client side and the server's IP address should be specified. 

First, Server_ProxTrace.java should be run to make the server listen posibble clients. Whenever Client_Square_Loss is run, server first checks wheather the client has a valid address or not. If the client is valid, communication starts. 

In this first version, a specific problem setting for multi-task learning, which is square l2 loss objective and trace norm regularizer, has been implemented. Several proximal operators and objective functions are also added.

Splitting algorithm used in this implementation is Backward-Forward splitting where proximal step takes place in the server and the gradient step is performed in the client. Backward step is performed in ServerThread_Trace.java which creates a thread for each client to perform server side operations related to each client. To implement different splitting schemes Client_Square_Loss.java and ServerThread_Trace.java can be modified.

# Requirements to Use the Java Code

Java compiler compliance level should be at least 1.6.
