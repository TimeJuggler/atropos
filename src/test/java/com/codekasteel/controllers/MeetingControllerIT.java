package com.codekasteel.controllers;

import com.codekasteel.entities.Meeting;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MeetingControllerIT {

    @Autowired
    MockMvc mockMvc;
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    @Before
    public void setUp() throws Exception {

        Meeting meeting = new Meeting();
        meeting.setAttendees(Collections.singletonList("898"));
        meeting.setFromDate(DATE_FORMATTER.parse("20-03-2018 10:00"));
        meeting.setToDate(DATE_FORMATTER.parse("20-03-2018 11:00"));

        saveMeeting(meeting, HttpStatus.CREATED);

        meeting = new Meeting();
        meeting.setAttendees(Collections.singletonList("898"));
        meeting.setFromDate(DATE_FORMATTER.parse("20-03-2018 12:00"));
        meeting.setToDate(DATE_FORMATTER.parse("20-03-2018 12:30"));

        saveMeeting(meeting, HttpStatus.CREATED);

        meeting = new Meeting();
        meeting.setAttendees(Collections.singletonList("898"));
        meeting.setFromDate(DATE_FORMATTER.parse("20-03-2018 12:30"));
        meeting.setToDate(DATE_FORMATTER.parse("20-03-2018 14:00"));

        saveMeeting(meeting, HttpStatus.CREATED);
    }

    private Meeting saveMeeting(Meeting meeting, HttpStatus httpStatus) throws Exception {

        MvcResult result = mockMvc.perform(post("/meeting")
                .content(asJsonString(meeting))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(httpStatus.value()))
                .andReturn();

        String response = result.getResponse().getContentAsString();

        return getMeetingFromJson(response);
    }

    private Meeting getMeetingFromJson(String response) throws java.io.IOException {
        Meeting created = null;

        if(!response.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            created = mapper.readValue(response, Meeting.class);
        }
        return created;
    }

    @Test
    public void shouldAddMeeting() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setAttendees(Collections.singletonList("898"));
        meeting.setFromDate(DATE_FORMATTER.parse("20-03-2018 14:30"));
        meeting.setToDate(DATE_FORMATTER.parse("20-03-2018 15:00"));


        Meeting created = saveMeeting(meeting, HttpStatus.CREATED);
        assertThat(created.getId(), greaterThan(0L));
    }

    @Test
    public void shouldNotAddMeetingWhenTimeSlotIsNotAvailable() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setAttendees(Collections.singletonList("898"));
        meeting.setFromDate(DATE_FORMATTER.parse("20-03-2018 10:30"));
        meeting.setToDate(DATE_FORMATTER.parse("20-03-2018 11:30"));


        Meeting created = saveMeeting(meeting, HttpStatus.CONFLICT);
        assertThat(created, is(nullValue()));
    }

    @Test
    public void shouldNotAddMeetingWhenTimeSlotIsSuperset() throws Exception {
        Meeting meeting = new Meeting();
        meeting.setAttendees(Collections.singletonList("898"));
        meeting.setFromDate(DATE_FORMATTER.parse("20-03-2018 08:00"));
        meeting.setToDate(DATE_FORMATTER.parse("20-03-2018 15:00"));


        Meeting created = saveMeeting(meeting, HttpStatus.CONFLICT);
        assertThat(created, is(nullValue()));
    }

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        final ObjectMapper mapper = new ObjectMapper();
        final String jsonContent = mapper.writeValueAsString(obj);

        System.out.println(jsonContent);

        return jsonContent;
    }
}