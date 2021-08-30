package org.batfish.vendor.check_point_management;

import static com.google.common.base.Preconditions.checkArgument;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.batfish.datamodel.Ip;
import org.batfish.datamodel.Ip6;

public final class AddressRange extends AddressSpace {

  public @Nullable Ip getIpv4AddressFirst() {
    return _ipv4AddressFirst;
  }

  public @Nullable Ip getIpv4AddressLast() {
    return _ipv4AddressLast;
  }

  public @Nullable Ip6 getIpv6AddressFirst() {
    return _ipv6AddressFirst;
  }

  public @Nullable Ip6 getIpv6AddressLast() {
    return _ipv6AddressLast;
  }

  @JsonCreator
  private static @Nonnull AddressRange create(
      @JsonProperty(PROP_IPV4_ADDRESS_FIRST) @Nullable Ip ipv4AddressFirst,
      @JsonProperty(PROP_IPV4_ADDRESS_LAST) @Nullable Ip ipv4AddressLast,
      @JsonProperty(PROP_IPV6_ADDRESS_FIRST) @Nullable Ip6 ipv6AddressFirst,
      @JsonProperty(PROP_IPV6_ADDRESS_LAST) @Nullable Ip6 ipv6AddressLast,
      @JsonProperty(PROP_NAME) @Nullable String name,
      @JsonProperty(PROP_UID) @Nullable Uid uid) {
    checkArgument(name != null, "Missing %s", PROP_NAME);
    checkArgument(uid != null, "Missing %s", PROP_UID);
    return new AddressRange(
        ipv4AddressFirst, ipv4AddressLast, ipv6AddressFirst, ipv6AddressLast, name, uid);
  }

  @VisibleForTesting
  public AddressRange(
      @Nullable Ip ipv4AddressFirst,
      @Nullable Ip ipv4AddressLast,
      @Nullable Ip6 ipv6AddressFirst,
      @Nullable Ip6 ipv6AddressLast,
      String name,
      Uid uid) {
    super(name, uid);
    _ipv4AddressFirst = ipv4AddressFirst;
    _ipv4AddressLast = ipv4AddressLast;
    _ipv6AddressFirst = ipv6AddressFirst;
    _ipv6AddressLast = ipv6AddressLast;
  }

  @Override
  public boolean equals(Object o) {
    if (!baseEquals(o)) {
      return false;
    }
    AddressRange that = (AddressRange) o;
    return Objects.equals(_ipv4AddressFirst, that._ipv4AddressFirst)
        && Objects.equals(_ipv4AddressLast, that._ipv4AddressLast)
        && Objects.equals(_ipv6AddressFirst, that._ipv6AddressFirst)
        && Objects.equals(_ipv6AddressLast, that._ipv6AddressLast);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        baseHashcode(), _ipv4AddressFirst, _ipv4AddressLast, _ipv6AddressFirst, _ipv6AddressLast);
  }

  @Override
  public String toString() {
    return baseToStringHelper()
        .omitNullValues()
        .add(PROP_IPV4_ADDRESS_FIRST, _ipv4AddressFirst)
        .add(PROP_IPV4_ADDRESS_LAST, _ipv4AddressLast)
        .add(PROP_IPV6_ADDRESS_FIRST, _ipv6AddressFirst)
        .add(PROP_IPV6_ADDRESS_LAST, _ipv6AddressLast)
        .toString();
  }

  private static final String PROP_IPV4_ADDRESS_FIRST = "ipv4-address-first";
  private static final String PROP_IPV4_ADDRESS_LAST = "ipv4-address-last";
  private static final String PROP_IPV6_ADDRESS_FIRST = "ipv6-address-first";
  private static final String PROP_IPV6_ADDRESS_LAST = "ipv6-address-last";

  private final @Nullable Ip _ipv4AddressFirst;
  private final @Nullable Ip _ipv4AddressLast;
  private final @Nullable Ip6 _ipv6AddressFirst;
  private final @Nullable Ip6 _ipv6AddressLast;
}