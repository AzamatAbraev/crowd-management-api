package com.crowdmanagement.crowdmanagementapi.timetable;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private List<TimetableEntry> cachedTimetable = new ArrayList<>();
    private Map<String, List<TimetableEntry>> classIndex = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            refreshTimetable();
            System.out.println("Timetable successfully cached in memory.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshTimetable() throws Exception {
        XmlMapper xmlMapper = new XmlMapper();
        File file = new File("src/main/resources/timetable.xml");
        TimetableXml data = xmlMapper.readValue(file, TimetableXml.class);

        Map<String, String[]> periodMap = data.getPeriods().stream()
                .collect(Collectors.toMap(XmlPeriod::getPeriod, p -> new String[]{p.getStarttime(), p.getEndtime()}));

        Map<String, XmlSubject> subjectMap = data.getSubjects().stream()
                .collect(Collectors.toMap(XmlSubject::getId, s -> s));

        Map<String, String> classroomMap = data.getClassrooms().stream()
                .collect(Collectors.toMap(XmlClassroom::getId, XmlClassroom::getName));

        Map<String, String> classMap = data.getClasses().stream()
                .collect(Collectors.toMap(XmlClass::getId, XmlClass::getName));

        Map<String, String> teacherMap = data.getTeachers().stream()
                .collect(Collectors.toMap(XmlTeacher::getId, XmlTeacher::getName));

        Map<String, String> groupMap = data.getGroups().stream()
                .collect(Collectors.toMap(XmlGroup::getId, XmlGroup::getName));

        Map<String, String> dayMap = data.getDaysDefs().stream()
                .collect(Collectors.toMap(XmlDaysDef::getDays, XmlDaysDef::getName));

        Map<String, XmlLesson> lessonMap = data.getLessons().stream()
                .collect(Collectors.toMap(XmlLesson::getId, l -> l));

        this.cachedTimetable = data.getCards().stream().map(card -> {
            XmlLesson lesson = lessonMap.get(card.getLessonid());
            String[] times = periodMap.get(card.getPeriod());
            XmlSubject subject = subjectMap.get(lesson.getSubjectid());

            return TimetableEntry.builder()
                    .subject(subject != null ? subject.getName() : "Unknown")
                    .day(dayMap.getOrDefault(card.getDays(), "Unknown"))
                    .startTime(times != null ? times[0] : "")
                    .endTime(times != null ? times[1] : "")
                    .classroom(classroomMap.getOrDefault(card.getClassroomids(), "N/A"))
                    .className(resolveNames(lesson.getClassids(), classMap))
                    .teacherName(resolveNames(lesson.getTeacherids(), teacherMap))
                    .groupName(resolveNames(lesson.getGroupids(), groupMap))
                    .build();
        }).collect(Collectors.toList());

        this.classIndex = cachedTimetable.stream()
                .collect(Collectors.groupingBy(TimetableEntry::getClassName));
    }

    public List<TimetableEntry> getFilteredTimetable(String day, String className,
                                                     String teacher, String subject,
                                                     String classroom) {
        return cachedTimetable.stream()
                .filter(entry -> day == null || entry.getDay().equalsIgnoreCase(day))
                .filter(entry -> className == null || entry.getClassName().toLowerCase().contains(className.toLowerCase()))
                .filter(entry -> teacher == null || entry.getTeacherName().toLowerCase().contains(teacher.toLowerCase()))
                .filter(entry -> subject == null || entry.getSubject().toLowerCase().contains(subject.toLowerCase()))
                .filter(entry -> classroom == null || entry.getClassroom().equalsIgnoreCase(classroom))
                .collect(Collectors.toList());
    }

    // Helper to turn "ID1,ID2" into "Name1, Name2"
    private String resolveNames(String ids, Map<String, String> lookup) {
        if (ids == null || ids.isEmpty()) return "N/A";
        return Arrays.stream(ids.split(","))
                .map(id -> lookup.getOrDefault(id, "Unknown"))
                .collect(Collectors.joining(", "));
    }

    public List<TimetableEntry> getProcessedTimetable() {
        return cachedTimetable;
    }

    public List<TimetableEntry> getByClassName(String className) {
        return classIndex.getOrDefault(className, Collections.emptyList());
    }
}