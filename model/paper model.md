﻿

<br/>
<br/>
<br/>

$$
b_{i}^{k}=
 \begin{cases}
   w^{k}  &\mbox{$i=o(k)$}\\
   -w^{k}  &\mbox{$i=d(k)$}\\
   0&\mbox{otherwise}
   \end{cases}
$$


***Minimize***
$\sum_{\tau\in \theta} \sum_{(i,j)\in A} v_{ij}r_{ij}^{\tau}z_{\tau}+ \sum_{p\in P}\sum_{l\in L}\sum_{\tau\in \theta _{pl}}  f_{pl}z _{\tau}+\sum_{k\in K} \sum_{(i,j)\in A} c_{ij}x_{ij}^{k}$

***Subject to:***
   $\sum_{j:(i,j)\in A} x_{ij}^{k}-\sum_{j:(j,i)\in A} x_{ji}^{k}=b_{i}^{k}
    \qquad \forall k\in K, \forall i\in N\qquad (1)$
   
$\sum_{k\in K}x_{ij}^{k} \leqslant \sum_{p\in P}\sum_{\tau\in \theta _{p}} C_{p}r_{ij}^{\tau}z_{\tau} \qquad \forall(i,j)\in E\qquad(2)$

   $\sum_{\tau\in \theta_{pl}}z_{\tau} \leqslant ub_{pl}\qquad \forall p\in P,\forall l\in L \qquad(3)$

   $x_{ij}^{k}\geqslant 0\qquad \forall k\in K,(i,j) \in A\qquad(4)$
   $z_{\tau}\in Z\qquad \forall \tau\in \theta\qquad(5)$
<br/>
$z_{\tau}\geqslant 0\qquad \forall \tau\in \theta\qquad(5b)$

$\sum_{\tau\in \theta}g_{s}^{\tau}z_{\tau} \leqslant /\geqslant constant \qquad \forall(s)\in Service4AllBranchSet1\qquad(6)$

$\sum_{\tau\in \theta_{pl}}g_{s}^{\tau}z_{\tau} \leqslant /\geqslant constant \qquad \forall(s,p,l)\in ServiceBranchSet2\qquad(7)$

$\sum_{\tau\in \theta_{pl}}r_{ij}^{\tau}z_{\tau} \leqslant /\geqslant constant \qquad \forall((i,j),p,l)\in ServiceArcBranchSet3\qquad(8)$

$x_{ij}^{k} \leqslant w^{k}\sum_{\tau\in \theta}r_{ij}^{\tau}z_{\tau} \qquad \forall(i,j)\in E, \forall k\in K\qquad(9)$