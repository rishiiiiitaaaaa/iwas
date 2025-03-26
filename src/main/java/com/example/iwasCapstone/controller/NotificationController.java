package com.example.iwasCapstone.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iwasCapstone.model.Notification;
import com.example.iwasCapstone.repository.NotificationRepository;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/{employeeId}")
    public List<Notification> getNotifications(@PathVariable Integer employeeId) {
        return notificationRepository.findByEmployeeId(employeeId);
    }

    @PostMapping("/create")
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationRepository.save(notification);
    }

    @PutMapping("/read/{id}")
    public void markNotificationAsRead(@PathVariable Integer id) {
        Optional<Notification> optionalNotification = notificationRepository.findById(id);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setStatus("Read");
            notificationRepository.save(notification);
        }
    }
}
