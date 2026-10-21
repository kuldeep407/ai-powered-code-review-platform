package com.example.codereview.service;

import com.example.codereview.entity.Activity;
import com.example.codereview.entity.User;
import com.example.codereview.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void record(User user, String action, String details) {
        if (user != null) {
            Activity activity = new Activity(user, action, details);
            activityRepository.save(activity);
        }
    }
}
