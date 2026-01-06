package com.crowdmanagement.crowdmanagementapi.timetable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.*;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "timetable")
public class TimetableXml {
    @JacksonXmlElementWrapper(localName = "periods")
    @JacksonXmlProperty(localName = "period")
    private List<XmlPeriod> periods;

    @JacksonXmlElementWrapper(localName = "daysdefs")
    @JacksonXmlProperty(localName = "daysdef")
    private List<XmlDaysDef> daysDefs;

    @JacksonXmlElementWrapper(localName = "subjects")
    @JacksonXmlProperty(localName = "subject")
    private List<XmlSubject> subjects;

    @JacksonXmlElementWrapper(localName = "lessons")
    @JacksonXmlProperty(localName = "lesson")
    private List<XmlLesson> lessons;

    @JacksonXmlElementWrapper(localName = "cards")
    @JacksonXmlProperty(localName = "card")
    private List<XmlCard> cards;

    @JacksonXmlElementWrapper(localName = "classrooms")
    @JacksonXmlProperty(localName = "classroom")
    private List<XmlClassroom> classrooms;

    @JacksonXmlElementWrapper(localName = "classes")
    @JacksonXmlProperty(localName = "class")
    private List<XmlClass> classes;

    @JacksonXmlElementWrapper(localName = "teachers")
    @JacksonXmlProperty(localName = "teacher")
    private List<XmlTeacher> teachers;

    @JacksonXmlElementWrapper(localName = "groups")
    @JacksonXmlProperty(localName = "group")
    private List<XmlGroup> groups;

}

@Data @JsonIgnoreProperties(ignoreUnknown = true)
class XmlTeacher {
    @JacksonXmlProperty(isAttribute = true) private String id;
    @JacksonXmlProperty(isAttribute = true) private String name;
}

@Data @JsonIgnoreProperties(ignoreUnknown = true)
class XmlGroup {
    @JacksonXmlProperty(isAttribute = true) private String id;
    @JacksonXmlProperty(isAttribute = true) private String name;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlPeriod {
    @JacksonXmlProperty(isAttribute = true) private String period;
    @JacksonXmlProperty(isAttribute = true) private String starttime;
    @JacksonXmlProperty(isAttribute = true) private String endtime;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlClassroom {
    @JacksonXmlProperty(isAttribute = true) private String id;
    @JacksonXmlProperty(isAttribute = true) private String name;
    @JacksonXmlProperty(isAttribute = true, localName = "short")
    private String shortName;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlClass {
    @JacksonXmlProperty(isAttribute = true) private String id;
    @JacksonXmlProperty(isAttribute = true) private String name;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlDaysDef {
    @JacksonXmlProperty(isAttribute = true) private String name;
    @JacksonXmlProperty(isAttribute = true) private String days;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlSubject {
    @JacksonXmlProperty(isAttribute = true) private String id;
    @JacksonXmlProperty(isAttribute = true) private String name;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlLesson {
    @JacksonXmlProperty(isAttribute = true) private String id;
    @JacksonXmlProperty(isAttribute = true) private String subjectid;
    @JacksonXmlProperty(isAttribute = true) private String classids;   // Multiple IDs possible
    @JacksonXmlProperty(isAttribute = true) private String teacherids; // Multiple IDs possible
    @JacksonXmlProperty(isAttribute = true) private String groupids;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class XmlCard {
    @JacksonXmlProperty(isAttribute = true) private String lessonid;
    @JacksonXmlProperty(isAttribute = true) private String period;
    @JacksonXmlProperty(isAttribute = true) private String days;
    @JacksonXmlProperty(isAttribute = true) private String classroomids;
}