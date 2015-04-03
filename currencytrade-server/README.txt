-> Limits rate based on:
	1. IP address of caller: web.xml-> rate
	2. service operation executions: config.properties or Program args : limit.rate

-> it's supposed to be run inside of HTTPS
	-> self-signed for simplicity

-> pass is hashed with HMACS with salt

GUI is the ugliest frontend in the history of mankind. Two listings:
	1. root of application through index.jsp
	2. using REST GET -> XML

Rate limiter for IP is draft
Rate limiter for service operation is draft
Cleaner thread should be handled otherwise
File Writer should be handled in queue periodically
Some DI mechanism (Spring...)
GUI with something more eye appealing (Vaadin...)
Security is partially done
Nothing is commented...

Everything is an almost zero tested draft...one giant non professional bunch of very erratic flow of thoughts..done in very limited time

Should be used in HTTPS.. currently only self-signed certificate..import it to cacerts