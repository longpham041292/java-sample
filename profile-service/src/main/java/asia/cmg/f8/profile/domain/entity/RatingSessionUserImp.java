package asia.cmg.f8.profile.domain.entity;

import java.util.List;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import asia.cmg.f8.common.spec.view.RatingSessionUser;



@SuppressWarnings({"CheckReturnValue", "PMD", "all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "AbstractRatingSessionUserImpl"})
@Immutable
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RatingSessionUserImp extends AbstractRatingSessionUserImpl {
  private final @Nullable String comment;
  private final String fullName;
  private final String id;
  private final String picture;
  private final String reaction;
  private final ImmutableList<String> reasons;
  private final Long sessionDate;
  private final Double stars;

  private RatingSessionUserImp(
      @Nullable String comment,
      String fullName,
      String id,
      String picture,
      String reaction,
      ImmutableList<String> reasons,
      Long sessionDate,
      Double stars) {
    this.comment = comment;
    this.fullName = fullName;
    this.id = id;
    this.picture = picture;
    this.reaction = reaction;
    this.reasons = reasons;
    this.sessionDate = sessionDate;
    this.stars = stars;
  }

  /**
   * @return The value of the {@code comment} attribute
   */
  @Override
  public @Nullable String getComment() {
    return comment;
  }

  /**
   * @return The value of the {@code fullName} attribute
   */
  @Override
  public String getFullName() {
    return fullName;
  }

  /**
   * @return The value of the {@code id} attribute
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * @return The value of the {@code picture} attribute
   */
  @Override
  public String getPicture() {
    return picture;
  }

  /**
   * @return The value of the {@code reaction} attribute
   */
  @Override
  public String getReaction() {
    return reaction;
  }

  /**
   * @return The value of the {@code reasons} attribute
   */
  @Override
  public ImmutableList<String> getReasons() {
    return reasons;
  }

  /**
   * @return The value of the {@code sessionDate} attribute
   */
  @Override
  public Long getSessionDate() {
    return sessionDate;
  }

  /**
   * @return The value of the {@code stars} attribute
   */
  @Override
  public Double getStars() {
    return stars;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getComment() comment} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for comment (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withComment(@Nullable String value) {
    if (Objects.equal(this.comment, value)) return this;
    return new RatingSessionUserImp(
        value,
        this.fullName,
        this.id,
        this.picture,
        this.reaction,
        this.reasons,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getFullName() fullName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for fullName
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withFullName(String value) {
    if (this.fullName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "fullName");
    return new RatingSessionUserImp(
        this.comment,
        newValue,
        this.id,
        this.picture,
        this.reaction,
        this.reasons,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getId() id} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for id
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withId(String value) {
    if (this.id.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "id");
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        newValue,
        this.picture,
        this.reaction,
        this.reasons,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getPicture() picture} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for picture
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withPicture(String value) {
    if (this.picture.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "picture");
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        this.id,
        newValue,
        this.reaction,
        this.reasons,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getReaction() reaction} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for reaction
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withReaction(String value) {
    if (this.reaction.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "reaction");
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        this.id,
        this.picture,
        newValue,
        this.reasons,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link AbstractRatingSessionUserImpl#getReasons() reasons}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final RatingSessionUserImp withReasons(String... elements) {
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        this.id,
        this.picture,
        this.reaction,
        newValue,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link AbstractRatingSessionUserImpl#getReasons() reasons}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of reasons elements to set
   * @return A modified copy of {@code this} object
   */
  public final RatingSessionUserImp withReasons(Iterable<String> elements) {
    if (this.reasons == elements) return this;
    ImmutableList<String> newValue = ImmutableList.copyOf(elements);
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        this.id,
        this.picture,
        this.reaction,
        newValue,
        this.sessionDate,
        this.stars);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getSessionDate() sessionDate} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for sessionDate
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withSessionDate(Long value) {
    if (this.sessionDate.equals(value)) return this;
    Long newValue = Preconditions.checkNotNull(value, "sessionDate");
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        this.id,
        this.picture,
        this.reaction,
        this.reasons,
        newValue,
        this.stars);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link AbstractRatingSessionUserImpl#getStars() stars} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for stars
   * @return A modified copy of the {@code this} object
   */
  public final RatingSessionUserImp withStars(Double value) {
    if (this.stars.equals(value)) return this;
    Double newValue = Preconditions.checkNotNull(value, "stars");
    return new RatingSessionUserImp(
        this.comment,
        this.fullName,
        this.id,
        this.picture,
        this.reaction,
        this.reasons,
        this.sessionDate,
        newValue);
  }

  /**
   * This instance is equal to all instances of {@code RatingSessionUserImpl} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof RatingSessionUserImp
        && equalTo((RatingSessionUserImp) another);
  }

  private boolean equalTo(RatingSessionUserImp another) {
    return Objects.equal(comment, another.comment)
        && fullName.equals(another.fullName)
        && id.equals(another.id)
        && picture.equals(another.picture)
        && reaction.equals(another.reaction)
        && reasons.equals(another.reasons)
        && sessionDate.equals(another.sessionDate)
        && stars.equals(another.stars);
  }

  /**
   * Computes a hash code from attributes: {@code comment}, {@code fullName}, {@code id}, {@code picture}, {@code reaction}, {@code reasons}, {@code sessionDate}, {@code stars}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + Objects.hashCode(comment);
    h = h * 17 + fullName.hashCode();
    h = h * 17 + id.hashCode();
    h = h * 17 + picture.hashCode();
    h = h * 17 + reaction.hashCode();
    h = h * 17 + reasons.hashCode();
    h = h * 17 + sessionDate.hashCode();
    h = h * 17 + stars.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code RatingSessionUserImpl} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("RatingSessionUserImpl")
        .omitNullValues()
        .add("comment", comment)
        .add("fullName", fullName)
        .add("id", id)
        .add("picture", picture)
        .add("reaction", reaction)
        .add("reasons", reasons)
        .add("sessionDate", sessionDate)
        .add("stars", stars)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link AbstractRatingSessionUserImpl} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable RatingSessionUserImpl instance
   */
  public static RatingSessionUserImp copyOf(AbstractRatingSessionUserImpl instance) {
    if (instance instanceof RatingSessionUserImp) {
      return (RatingSessionUserImp) instance;
    }
    return RatingSessionUserImp.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link RatingSessionUserImp RatingSessionUserImpl}.
   * @return A new RatingSessionUserImpl builder
   */
  public static RatingSessionUserImp.Builder builder() {
    return new RatingSessionUserImp.Builder();
  }

  /**
   * Builds instances of type {@link RatingSessionUserImp RatingSessionUserImpl}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_FULL_NAME = 0x1L;
    private static final long INIT_BIT_ID = 0x2L;
    private static final long INIT_BIT_PICTURE = 0x4L;
    private static final long INIT_BIT_REACTION = 0x8L;
    private static final long INIT_BIT_SESSION_DATE = 0x10L;
    private static final long INIT_BIT_STARS = 0x20L;
    private long initBits = 0x3fL;

    private @Nullable String comment;
    private @Nullable String fullName;
    private @Nullable String id;
    private @Nullable String picture;
    private @Nullable String reaction;
    private ImmutableList.Builder<String> reasons = ImmutableList.builder();
    private @Nullable Long sessionDate;
    private @Nullable Double stars;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code asia.cmg.f8.common.spec.view.RatingSessionUser} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(RatingSessionUser instance) {
      Preconditions.checkNotNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    /**
     * Fill a builder with attribute values from the provided {@code asia.cmg.f8.social.entity.AbstractRatingSessionUserImpl} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(AbstractRatingSessionUserImpl instance) {
      Preconditions.checkNotNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    private void from(Object object) {
      if (object instanceof RatingSessionUser) {
        RatingSessionUser instance = (RatingSessionUser) object;
        reaction(instance.getReaction());
        addAllReasons(instance.getReasons());
        sessionDate(instance.getSessionDate());
        fullName(instance.getFullName());
        @Nullable String commentValue = instance.getComment();
        if (commentValue != null) {
          comment(commentValue);
        }
        id(instance.getId());
        stars(instance.getStars());
        picture(instance.getPicture());
      }
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getComment() comment} attribute.
     * @param comment The value for comment (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder comment(@Nullable String comment) {
      this.comment = comment;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getFullName() fullName} attribute.
     * @param fullName The value for fullName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder fullName(String fullName) {
      this.fullName = Preconditions.checkNotNull(fullName, "fullName");
      initBits &= ~INIT_BIT_FULL_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getId() id} attribute.
     * @param id The value for id 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder id(String id) {
      this.id = Preconditions.checkNotNull(id, "id");
      initBits &= ~INIT_BIT_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getPicture() picture} attribute.
     * @param picture The value for picture 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder picture(String picture) {
      this.picture = Preconditions.checkNotNull(picture, "picture");
      initBits &= ~INIT_BIT_PICTURE;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getReaction() reaction} attribute.
     * @param reaction The value for reaction 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder reaction(String reaction) {
      this.reaction = Preconditions.checkNotNull(reaction, "reaction");
      initBits &= ~INIT_BIT_REACTION;
      return this;
    }

    /**
     * Adds one element to {@link AbstractRatingSessionUserImpl#getReasons() reasons} list.
     * @param element A reasons element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addReasons(String element) {
      this.reasons.add(element);
      return this;
    }

    /**
     * Adds elements to {@link AbstractRatingSessionUserImpl#getReasons() reasons} list.
     * @param elements An array of reasons elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addReasons(String... elements) {
      this.reasons.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link AbstractRatingSessionUserImpl#getReasons() reasons} list.
     * @param elements An iterable of reasons elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder reasons(Iterable<String> elements) {
      this.reasons = ImmutableList.builder();
      return addAllReasons(elements);
    }

    /**
     * Adds elements to {@link AbstractRatingSessionUserImpl#getReasons() reasons} list.
     * @param elements An iterable of reasons elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllReasons(Iterable<String> elements) {
      this.reasons.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getSessionDate() sessionDate} attribute.
     * @param sessionDate The value for sessionDate 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder sessionDate(Long sessionDate) {
      this.sessionDate = Preconditions.checkNotNull(sessionDate, "sessionDate");
      initBits &= ~INIT_BIT_SESSION_DATE;
      return this;
    }

    /**
     * Initializes the value for the {@link AbstractRatingSessionUserImpl#getStars() stars} attribute.
     * @param stars The value for stars 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder stars(Double stars) {
      this.stars = Preconditions.checkNotNull(stars, "stars");
      initBits &= ~INIT_BIT_STARS;
      return this;
    }

    /**
     * Builds a new {@link RatingSessionUserImp RatingSessionUserImpl}.
     * @return An immutable instance of RatingSessionUserImpl
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public RatingSessionUserImp build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new RatingSessionUserImp(comment, fullName, id, picture, reaction, reasons.build(), sessionDate, stars);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_FULL_NAME) != 0) attributes.add("fullName");
      if ((initBits & INIT_BIT_ID) != 0) attributes.add("id");
      if ((initBits & INIT_BIT_PICTURE) != 0) attributes.add("picture");
      if ((initBits & INIT_BIT_REACTION) != 0) attributes.add("reaction");
      if ((initBits & INIT_BIT_SESSION_DATE) != 0) attributes.add("sessionDate");
      if ((initBits & INIT_BIT_STARS) != 0) attributes.add("stars");
      return "Cannot build RatingSessionUserImpl, some of required attributes are not set " + attributes;
    }
  }

}
