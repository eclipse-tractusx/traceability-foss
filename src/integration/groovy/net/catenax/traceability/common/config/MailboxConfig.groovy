package net.catenax.traceability.common.config

import com.icegreen.greenmail.spring.GreenMailBean
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class MailboxConfig {

	@Bean
	GreenMailBean greenMailBean() {
		GreenMailBean mailBean = new GreenMailBean()
		mailBean.setUsers(["notifications:password@catena-x.net"])
		mailBean
	}
}
