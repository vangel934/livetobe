-> Rate Limit : 
	->Limits rate based on:
	1. IP address of caller: web.xml-> rate
	2. service operation executions: config.properties or Program args : limit.rate

-> Security
	-> it's supposed to be run inside of HTTPS
		-> self-signed for simplicity -> import cf.cer to cacerts
	-> currently sends user/pass, however it should not. Auth should be in other service or module. It should work with tickets only
	-> stored pass is hashed with HMAC with salt

-> GUI is the ugliest frontend in the history of mankind. Two listings:
	1. root of application through index.jsp (JSP)
	2. using REST GET (XML)

Rate limiter for IP is draft
Rate limiter for service operation is draft
Cleaner thread should be handled otherwise
File Writer should be handled in queue periodically
Some DI mechanism (Spring...)
GUI with something more eye appealing (Vaadin...)
Security is partially done
Nothing is commented...

Everything is one giant non professional bunch of very erratic flow of thoughts..done in very limited time
