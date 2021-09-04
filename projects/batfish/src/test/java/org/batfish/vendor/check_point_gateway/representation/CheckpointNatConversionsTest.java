package org.batfish.vendor.check_point_gateway.representation;

import static org.batfish.datamodel.transformation.TransformationStep.assignSourceIp;
import static org.batfish.datamodel.transformation.TransformationStep.assignSourcePort;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.NAT_PORT_FIRST;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.NAT_PORT_LAST;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.checkValidManualHide;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.getApplicableNatRules;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.getManualNatRules;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.manualHideRuleTransformation;
import static org.batfish.vendor.check_point_gateway.representation.CheckpointNatConversions.manualHideTransformationSteps;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import org.batfish.common.Warnings;
import org.batfish.datamodel.HeaderSpace;
import org.batfish.datamodel.Ip;
import org.batfish.datamodel.acl.MatchHeaderSpace;
import org.batfish.datamodel.transformation.Transformation;
import org.batfish.vendor.check_point_management.CpmiAnyObject;
import org.batfish.vendor.check_point_management.GatewayOrServer;
import org.batfish.vendor.check_point_management.GatewayOrServerPolicy;
import org.batfish.vendor.check_point_management.Host;
import org.batfish.vendor.check_point_management.NatMethod;
import org.batfish.vendor.check_point_management.NatRule;
import org.batfish.vendor.check_point_management.NatRulebase;
import org.batfish.vendor.check_point_management.NatSettings;
import org.batfish.vendor.check_point_management.Original;
import org.batfish.vendor.check_point_management.PolicyTargets;
import org.batfish.vendor.check_point_management.ServiceTcp;
import org.batfish.vendor.check_point_management.SimpleGateway;
import org.batfish.vendor.check_point_management.TypedManagementObject;
import org.batfish.vendor.check_point_management.Uid;
import org.junit.Test;

/** Test of {@link CheckpointNatConversions}. */
public final class CheckpointNatConversionsTest {
  public static final NatSettings NAT_SETTINGS_TEST_INSTANCE =
      new NatSettings(true, "gateway", "All", "hide");

  @Test
  public void testGetApplicableNatRules() {
    GatewayOrServer gateway =
        new SimpleGateway(
            Ip.ZERO, "foo", ImmutableList.of(), new GatewayOrServerPolicy(null, null), UID);
    NatRule enabledRule =
        new NatRule(
            true,
            "",
            true,
            ImmutableList.of(),
            NatMethod.STATIC,
            UID,
            UID,
            UID,
            1,
            UID,
            UID,
            UID,
            UID);
    NatRule disabledRule =
        new NatRule(
            true,
            "",
            false,
            ImmutableList.of(),
            NatMethod.STATIC,
            UID,
            UID,
            UID,
            1,
            UID,
            UID,
            UID,
            UID);
    NatRulebase natRulebase =
        new NatRulebase(ImmutableMap.of(), ImmutableList.of(enabledRule, disabledRule), UID);
    assertThat(
        getApplicableNatRules(natRulebase, gateway).collect(ImmutableList.toImmutableList()),
        equalTo(ImmutableList.of(enabledRule)));
  }

  @Test
  public void testGetManualNatRules() {
    GatewayOrServer gateway =
        new SimpleGateway(
            Ip.ZERO, "foo", ImmutableList.of(), new GatewayOrServerPolicy(null, null), UID);
    NatRule autoRule =
        new NatRule(
            true,
            "",
            true,
            ImmutableList.of(),
            NatMethod.STATIC,
            UID,
            UID,
            UID,
            1,
            UID,
            UID,
            UID,
            UID);
    NatRule manualRule =
        new NatRule(
            false,
            "",
            true,
            ImmutableList.of(),
            NatMethod.STATIC,
            UID,
            UID,
            UID,
            1,
            UID,
            UID,
            UID,
            UID);
    NatRulebase natRulebase =
        new NatRulebase(ImmutableMap.of(), ImmutableList.of(autoRule, manualRule), UID);
    assertThat(
        getManualNatRules(natRulebase, gateway).collect(ImmutableList.toImmutableList()),
        equalTo(ImmutableList.of(manualRule)));
  }

  @Test
  public void testCheckValidManualHide() {
    Uid uid = Uid.of("1");
    TypedManagementObject addressSpace = new Host(Ip.ZERO, NAT_SETTINGS_TEST_INSTANCE, "foo", uid);
    TypedManagementObject service = new ServiceTcp("foo", "1", uid);
    Warnings warnings = new Warnings();

    assertFalse(checkValidManualHide(service, ORIG, ORIG, warnings));
    assertFalse(checkValidManualHide(addressSpace, addressSpace, ORIG, warnings));
    assertFalse(checkValidManualHide(addressSpace, ORIG, service, warnings));
    assertTrue(checkValidManualHide(addressSpace, ORIG, ORIG, warnings));
  }

  @Test
  public void testManualHideTransformationSteps() {
    Warnings warnings = new Warnings();
    {
      assertThat(
          manualHideTransformationSteps(POLICY_TARGETS, POLICY_TARGETS, POLICY_TARGETS, warnings),
          equalTo(Optional.empty()));
    }
    {
      Uid hostUid = Uid.of("1");
      Ip hostIp = Ip.parse("1.1.1.1");
      String hostname = "host";
      Host host = new Host(hostIp, NAT_SETTINGS_TEST_INSTANCE, hostname, hostUid);
      assertThat(
          manualHideTransformationSteps(host, ORIG, ORIG, warnings),
          equalTo(
              Optional.of(
                  ImmutableList.of(
                      assignSourceIp(hostIp), assignSourcePort(NAT_PORT_FIRST, NAT_PORT_LAST)))));
    }
  }

  @Test
  public void testManualHideRuleTransformation() {
    Warnings warnings = new Warnings();
    Uid hostUid = Uid.of("1");
    Ip hostIp = Ip.parse("1.1.1.1");
    String hostname = "host";
    Host host = new Host(hostIp, NAT_SETTINGS_TEST_INSTANCE, hostname, hostUid);
    {
      NatRule rule =
          new NatRule(
              false,
              "",
              true,
              ImmutableList.of(),
              NatMethod.HIDE,
              PT_UID,
              PT_UID,
              PT_UID,
              1,
              ORIG_UID,
              ORIG_UID,
              hostUid,
              UID);
      NatRulebase natRulebase =
          new NatRulebase(
              ImmutableMap.of(hostUid, host, PT_UID, POLICY_TARGETS, ORIG_UID, ORIG),
              ImmutableList.of(rule),
              UID);

      // invalid original fields
      assertThat(
          manualHideRuleTransformation(natRulebase, rule, warnings), equalTo(Optional.empty()));
    }
    {
      NatRule rule =
          new NatRule(
              false,
              "",
              true,
              ImmutableList.of(),
              NatMethod.HIDE,
              ANY_UID,
              ANY_UID,
              ANY_UID,
              1,
              PT_UID,
              PT_UID,
              PT_UID,
              UID);
      NatRulebase natRulebase =
          new NatRulebase(
              ImmutableMap.of(ANY_UID, ANY, PT_UID, POLICY_TARGETS), ImmutableList.of(rule), UID);

      // invalid translated fields
      assertThat(
          manualHideRuleTransformation(natRulebase, rule, warnings), equalTo(Optional.empty()));
    }
    {
      NatRule rule =
          new NatRule(
              false,
              "",
              true,
              ImmutableList.of(),
              NatMethod.HIDE,
              ANY_UID,
              ANY_UID,
              ANY_UID,
              1,
              ORIG_UID,
              ORIG_UID,
              hostUid,
              UID);
      NatRulebase natRulebase =
          new NatRulebase(
              ImmutableMap.of(ANY_UID, ANY, ORIG_UID, ORIG, hostUid, host),
              ImmutableList.of(rule),
              UID);

      assertThat(
          manualHideRuleTransformation(natRulebase, rule, warnings),
          equalTo(
              Optional.of(
                  Transformation.when(new MatchHeaderSpace(HeaderSpace.builder().build()))
                      .apply(
                          assignSourceIp(hostIp), assignSourcePort(NAT_PORT_FIRST, NAT_PORT_LAST))
                      .build())));
    }
  }

  private static final Uid UID = Uid.of("1001");
  private static final Uid PT_UID = Uid.of("1002");
  private static final PolicyTargets POLICY_TARGETS = new PolicyTargets(PT_UID);
  private static final Uid ANY_UID = Uid.of("1003");
  private static final CpmiAnyObject ANY = new CpmiAnyObject(ANY_UID);
  private static final Uid ORIG_UID = Uid.of("1004");
  private static final Original ORIG = new Original(ORIG_UID);
}