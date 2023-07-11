package com.test.pet.reports;

import dataModel.Status;
import dataModel.Tag;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;

import java.util.*;

@Listeners(com.test.pet.listeners.ListenerClass.class)
public class PetDataProvider {
    @DataProvider(name = "petData")
    public static Object[][] providePetData() {
        return new Object[][] {
                {"Dog", createTagsList("tag1", "tag2"), Status.available},
                {"Cat", createTagsList("tag3", "tag4"), Status.sold},
                {"Bird", createTagsList("tag5", "tag6"), Status.pending},
                {"Dog", createTagsList("tag1", "tag7"), Status.pending}
        };
    }

    private static List<Tag> createTagsList(String... tagNames) {
        List<Tag> tags = new ArrayList<>();
        for (String tagName : tagNames) {
            Tag tag = new Tag();
            tag.setId(1);
            tag.setName(tagName);
            tags.add(tag);
        }
        return tags;
    }

}
