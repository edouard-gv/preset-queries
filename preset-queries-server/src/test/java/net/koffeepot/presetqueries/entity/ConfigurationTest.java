package net.koffeepot.presetqueries.entity;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConfigurationTest {

    private Configuration h2Configuration;
    private Configuration emptyConfiguration;

    @Before
    public void setUp() throws Exception {
        //
        h2Configuration = new Configuration(
                new Long(1),
                "h2",
                "net.koffeepot.presetqueries.datasource.H2DataSourceFactory",
                "{\"db_close_delay\": -1, \"db_close_delay_in_string\": \"-1\" }"
        );
        emptyConfiguration = new Configuration(
                new Long(2),
                "h2",
                "net.koffeepot.presetqueries.datasource.H2DataSourceFactory",
                null
        );
    }

    @Test
    public void testNominalParams() throws Exception {
        assertThat(h2Configuration.isAttributeSet("db_close_delay")).isTrue();
        assertThat(h2Configuration.getStringAttribute("db_close_delay")).isEqualTo("-1");
    }

    //This is only for dummies who might do some typos in database params.
    //Note that only the first level of attributes key are case insensitive.
    @Test
    public void testCaseInsensitiveParams() throws Exception {
        assertThat(h2Configuration.isAttributeSet("DB_CLOSE_DELAY")).isTrue();
        assertThat(h2Configuration.getStringAttribute("dB_clOse_Delay")).isEqualTo("-1");
    }

    @Test
    public void testUnsetParams() throws Exception {
        assertThat(h2Configuration.isAttributeSet("unset_attribute")).isFalse();
        assertThatThrownBy(() -> h2Configuration
                .getStringAttribute("unset_attribute")).hasMessage("Attribute not found for h2 configuration: unset_attribute");
    }

    //Empty configuration test
    @Test
    public void testEmptyConfigurationParams() throws Exception {
        assertThat(emptyConfiguration.isAttributeSet("unset_attribute")).isFalse();
        assertThatThrownBy(() -> emptyConfiguration
                .getStringAttribute("unset_attribute")).hasMessage("Attribute not found for h2 configuration: unset_attribute");
    }

    //Int configuration test
    @Test
    public void testIntConfigurationParams() throws Exception {
        assertThat(h2Configuration.getStringAttribute("db_close_delay")).isEqualTo("-1");
        assertThat(h2Configuration.getStringAttribute("db_close_delay_in_string")).isEqualTo("-1");
        assertThat(h2Configuration.getIntegerAttribute("db_close_delay")).isEqualTo(new Integer(-1));
        assertThat(h2Configuration.getIntegerAttribute("db_close_delay_in_string")).isEqualTo(new Integer(-1));
    }
}
