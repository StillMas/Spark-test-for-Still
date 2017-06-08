//将极大似然函数中未观测到的量X替换为E(X,theta(t))，在后续极大似然函数对theta(t)的求导过程中应当将该theta(t)视作常数，而对原有的theta进行求导
for(i<-0 until 10)
{
theta=2*theta/(5*theta+1)
println(theta)
}