package org.batfish.vendor.check_point_management;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import org.apache.commons.lang3.SerializationUtils;
import org.batfish.common.util.BatfishObjectMapper;
import org.batfish.datamodel.Ip;
import org.junit.Test;

/** Test of {@link CpmiVsxClusterNetobj}. */
public final class CpmiVsxClusterNetobjTest {

  @Test
  public void testJacksonDeserialization() throws JsonProcessingException {
    String input =
        "{"
            + "\"GARBAGE\":0,"
            + "\"type\":\"CpmiVsxClusterNetobj\","
            + "\"uid\":\"0\","
            + "\"name\":\"foo\","
            + "\"ipv4-address\":\"0.0.0.0\","
            + "\"cluster-member-names\":[\"m1\"],"
            + "\"policy\":{"
            + "\"accessPolicyInstalled\": true,"
            + "\"accessPolicyName\": \"p1\","
            + "\"threatPolicyInstalled\": true,"
            + "\"threatPolicyName\": \"p2\""
            + "}" // policy
            + "}";
    assertThat(
        BatfishObjectMapper.ignoreUnknownMapper().readValue(input, CpmiVsxClusterNetobj.class),
        equalTo(
            new CpmiVsxClusterNetobj(
                ImmutableList.of("m1"),
                Ip.ZERO,
                "foo",
                new GatewayOrServerPolicy("p1", "p2"),
                Uid.of("0"))));
  }

  @Test
  public void testJavaSerialization() {
    CpmiVsxClusterNetobj obj =
        new CpmiVsxClusterNetobj(
            ImmutableList.of(), Ip.ZERO, "foo", GatewayOrServerPolicy.empty(), Uid.of("0"));
    assertEquals(obj, SerializationUtils.clone(obj));
  }

  @Test
  public void testEquals() {
    CpmiVsxClusterNetobj obj =
        new CpmiVsxClusterNetobj(
            ImmutableList.of(), Ip.ZERO, "foo", GatewayOrServerPolicy.empty(), Uid.of("0"));
    new EqualsTester()
        .addEqualityGroup(
            obj,
            new CpmiVsxClusterNetobj(
                ImmutableList.of(), Ip.ZERO, "foo", GatewayOrServerPolicy.empty(), Uid.of("0")))
        .addEqualityGroup(
            new CpmiVsxClusterNetobj(
                ImmutableList.of("m1"), Ip.ZERO, "foo", GatewayOrServerPolicy.empty(), Uid.of("0")))
        .addEqualityGroup(
            new CpmiVsxClusterNetobj(
                ImmutableList.of(),
                Ip.parse("0.0.0.1"),
                "foo",
                GatewayOrServerPolicy.empty(),
                Uid.of("0")))
        .addEqualityGroup(
            new CpmiVsxClusterNetobj(
                ImmutableList.of(), Ip.ZERO, "bar", GatewayOrServerPolicy.empty(), Uid.of("0")))
        .addEqualityGroup(
            new CpmiVsxClusterNetobj(
                ImmutableList.of(),
                Ip.ZERO,
                "foo",
                new GatewayOrServerPolicy("p1", null),
                Uid.of("0")))
        .addEqualityGroup(
            new CpmiVsxClusterNetobj(
                ImmutableList.of(), Ip.ZERO, "foo", GatewayOrServerPolicy.empty(), Uid.of("1")))
        .testEquals();
  }
}