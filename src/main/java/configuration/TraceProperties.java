package configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "trace.brave")
public class TraceProperties {

    @Value("${spring.application.name}")
    private String serverName;
    private String zipkin;
    private float rate;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getZipkin() {
        return zipkin;
    }

    public void setZipkin(String zipkin) {
        this.zipkin = zipkin;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}