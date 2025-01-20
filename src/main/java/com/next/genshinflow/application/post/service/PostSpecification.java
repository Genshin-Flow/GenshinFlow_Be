package com.next.genshinflow.application.post.service;

import com.next.genshinflow.domain.post.entity.PostEntity;
import com.next.genshinflow.enumeration.QuestCategory;
import com.next.genshinflow.enumeration.Region;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class PostSpecification {
    public static Specification<PostEntity> filterByRegions(List<Region> regions) {
        return (root, query, criteriaBuilder) -> {
            if (regions == null || regions.isEmpty()) return null;
            return root.get("region").in(regions);
        };
    }

    public static Specification<PostEntity> filterByQuestCategories(List<String> questCategories) {
        return (root, query, criteriaBuilder) -> {
            if (questCategories == null || questCategories.isEmpty()) return null;
            List<QuestCategory> categories = questCategories.stream()
                .map(QuestCategory::fromCategoryName)
                .toList();
            return root.get("questCategory").in(categories);
        };
    }

    public static Specification<PostEntity> filterByWorldLevels(List<Integer> worldLevels) {
        return (root, query, criteriaBuilder) -> {
            if (worldLevels == null || worldLevels.isEmpty()) return null;
            return root.get("worldLevel").in(worldLevels);
        };
    }
}