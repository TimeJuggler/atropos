package com.codekasteel.commands;

import com.codekasteel.entities.Meeting;
import com.codekasteel.repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingCommandService {

    @Autowired
    MeetingRepository repository;

    /**
     * Checks by retrieving all the meetings who's start and end
     * times overlap with the meeting's times. If the result is a non empty
     * list that means that there is an overlapping meeting and the result is {@code false}.
     *
     * @param meeting the meeting to check for
     * @return {@code true} if there is an overlapping meeting, {@code false} if otherwise.
     */
    public boolean isNotOverlapping(Meeting meeting) {

        List<Meeting> result = repository.findByDatesBetween(
        meeting.getFromDate(), meeting.getToDate(),
                meeting.getAttendees().get(0));

        return result.isEmpty();
    }
}
