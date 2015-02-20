/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010 Helmholtz-Zentrum Berlin fuer Materialien und Energie GmbH
 * Subject to license terms and conditions.
 */
package edu.msu.nscl.olog.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;

/**
 * Tags (collection) object that can be represented as XML/JSON in payload data.
 *
 * @author Eric Berryman taken from Ralph Lange <Ralph.Lange@bessy.de>
 */
@XmlRootElement(name = "tags")
public class Tags{

    private Collection<Tag> tags = new ArrayList<Tag>();

    /**
     * Creates a new instance of Tags.
     */
    public Tags() {
    }

    /**
     * Creates a new instance of Tags with one initial tag.
     *
     * @param tag initial element
     */
    public Tags(Tag tag) {
        tags.add(tag);
    }
    
    public Tags(Collection<Tag> tags) {
        this.tags.addAll(tags);
    }

    /**
     * Returns a collection of Tag.
     *
     * @return a collection of Tag
     */
    @XmlElement(name = "tag")
    @JsonProperty("tag")
    public Collection<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the collection of tags.
     *
     * @param items new tag collection
     */
    public void setTags(Collection<Tag> items) {
        this.tags = items;
    }

    /**
     * Adds a tag to the tag collection.
     *
     * @param item the Tag to add
     */
    public void addTag(Tag item) {
        this.tags.add(item);
    }

    /**
     * Creates a compact string representation for the log.
     *
     * @param data Tags to create the string representation for
     * @return string representation
     */
    public static String toLogger(Tags data) {
        if (data.getTags().size() == 0) {
            return "[None]";
        } else {
            StringBuilder s = new StringBuilder();
            s.append("[");
            for (Tag t : (Collection<Tag>)data.getTags()) {
                s.append(Tag.toLogger(t) + ",");
            }
            s.delete(s.length() - 1, s.length());
            s.append("]");
            return s.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tags)) return false;

        Tags tags1 = (Tags) o;

        if (tags != null ? !tags.equals(tags1.tags) : tags1.tags != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tags != null ? tags.hashCode() : 0;
    }
}
