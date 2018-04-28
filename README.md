<h2>Security</h2>
本项目是一个采用`Spring Security`的练习项目。可以实现前后端分离时候（web、app等）的权限控制<br/>
<h3>主要技术栈</h3>
<ul>
<li>Spring Boot</li>
<li>Spring Security</li>
<li>Mybatis</li>
<li>Redis</li>
<li>Druid数据源</li>
</ul>
<h3>快速上手</h3>
<h5>1) 把项目作为Maven工程导入IDE(Eclipse或Intellij IDEA)</h5>
<h5>2) 使用MySQL新建Security数据库（数据库脚本在/src/main/resources/security.sql）</h5>
<h5>3) 部署Redis服务器。本项目代码匹配的是本地Redis服务器，默认端口，数据库0，没有密码。如有不同修改application.yml文件中对应的配置即可<H5>
<h5>4) 启动项目。(使用IDE或者打成jar包或者使用Maven命令启动都可以)</h5>
<h3>项目介绍</h3>
<h5>a 项目启动以后，可以使用Postman进行接口测试。
<div>&nbsp;&nbsp;&nbsp;&nbsp;首先发送post请求到
    <a href="http://127.0.0.1/druid/">http://127.0.0.1/druid/</a>，这是druid的web监控页面，由于系统没有做权限控制，所以任何人都可以访问。</div>
<div>&nbsp;&nbsp;&nbsp;&nbsp;然后依次发送GET请求到
<a href="http://127.0.0.1/user/hello">http://127.0.0.1/user/hello</a>，
<a href="http://127.0.0.1/admin/hello">http://127.0.0.1/admin/hello</a>，
不登录是没有权限访问这俩接口的。
Spring Boot有一个全局异常处理，所以可以看到相应的错误JSON信息。
<div>&nbsp;&nbsp;&nbsp;&nbsp;Spring Security内置了一个/login的登录端口，一般来说不用自己写了。
我们发送post请求到/login接口，ContentType为application/json，JSON内容为
{"username":"admin","password":"111111"}。如果登录成功，可以在Response的Headers信息里拿到token,即键"Authorization"对应的值。
把它拷贝下来。<b>实际工程里，这里的返回信息可以进行改造,把用户的权限列表返还给前端，前端根据权限列表来渲染应用界面。
接下来我们验证Spring Security对用户访问权限的拦截。</b>
<div>
<div>&nbsp;&nbsp;&nbsp;&nbsp;拿到admin用户的token以后，我们再发送请求到
<a href="http://127.0.0.1/user/hello">http://127.0.0.1/user/hello</a>，
<a href="http://127.0.0.1/admin/hello">http://127.0.0.1/admin/hello</a>，
发现有了正确的响应结果。记得把键："Authorization",值：token的值，添加到Request的Headers信息里边。
然后我们登录user用户获取其token信息，再次尝试访问以上两个链接。会发现/admin/hello被拦截下来了，而/user/hello可以正常访问。
</div>
<div>
&nbsp;&nbsp;&nbsp;&nbsp;
测试登出功能，很简单，以admin用户为例，拿到其token，添加到Request Headers，发送请求到/logout，
提示我们登出成功。登出以后我们再拿这个token去访问之前两个hello链接就会提示我们token已经过期。 
</div>
</div>
<h5>b 一些细节</h5>
&nbsp;&nbsp;&nbsp;&nbsp;JSON Web Token细节这里就不展开讲了，详情请参阅<a href="https://jwt.io/">`JSON Web Tokens`</a>
<br/>
&nbsp;&nbsp;&nbsp;&nbsp;JSON Web Token是一个比较死板的东西。它被造出来以后，值和生命周期就固定了。比如设定2018-05-01 12:55:55这个时间点过期，我们就不能更改了。既不能提前让它失效，也不能让它的有效期延长。有一定的安全风险。
比如token被黑客截获以后，即便用户执行了登出动作，token只要还在生命周期内，黑客就可以拿着此token做各种请求，获取机密信息。<br/>
&nbsp;&nbsp;&nbsp;&nbsp;所以引入Redis来管理token，token自身的生命周期适当设置长一点，比如15天。Redis里以 username:token 的形式存储token，并且加入redis过期时间。每次请求都会刷新这个时间。每次登出的时候
Redis执行删除动作，删掉对应的 username:token。
<br/>
&nbsp;&nbsp;&nbsp;&nbsp;为了提高安全性，每个用户每次生成token使用的签名key都不相同。并将其存储到Redis中，形式为token:signingKey。生命周期与管理方法和上边的username:token相同。
<h5>c 思考</h5>
&nbsp;&nbsp;&nbsp;&nbsp;本项目还有诸多可以改进的地方。比如每次请求获取用户权限列表，现在是要去数据库去读取，可以换个方式，存储到Redis或者直接放token里边，减轻对数据库的负担。<br/>
&nbsp;&nbsp;&nbsp;&nbsp;这个安全体系可以加入Spring Cloud的Zuul网关模块，达到统一管理用户权限的效果。<br/>
&nbsp;&nbsp;&nbsp;&nbsp;最后Spring Cloud有自己的一套整合方案Spring-Cloud-Security。但是网上资料比较少。官方文档也只是简单介绍了一两页。<br/>
&nbsp;&nbsp;&nbsp;&nbsp;Spring Security体系比较强大，还有OAuth2等协议还没摸透，需要继续研究。另外一个著名权限管理模块Apache Shiro比Spring Security更简单易用，而且权限控制粒度可粗可细，丝毫不比Spring Security差。
Spring Security的优势在于它属于Spring家族，整合更方便，社区也更活跃。
<h5>d 最后</h5>
不足之处，欢迎小伙伴拍砖。
后续会有OAuth2.0+Spring Cloud的项目。