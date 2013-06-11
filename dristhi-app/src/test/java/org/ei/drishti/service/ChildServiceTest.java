package org.ei.drishti.service;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.TimelineEvent;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.dto.Action;
import org.ei.drishti.repository.AllTimelineEvents;
import org.ei.drishti.repository.ChildRepository;
import org.ei.drishti.repository.MotherRepository;
import org.ei.drishti.util.ActionBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.HashMap;

import static java.util.Arrays.asList;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class ChildServiceTest {
    @Mock
    private AllTimelineEvents allTimelineEvents;
    @Mock
    private ChildRepository childRepository;
    @Mock
    private MotherRepository motherRepository;

    private ChildService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildService(motherRepository, childRepository, allTimelineEvents);
    }

    @Test
    public void shouldHandlePNCVisitActionForChild() throws Exception {
        String caseId = "Case Child X";
        Action action = ActionBuilder.actionForChildPNCVisit(caseId, mapOf("some-key", "some-value"));

        service.pncVisit(action);

        verify(allTimelineEvents).add(TimelineEvent.forChildPNCVisit(caseId, "1", "2012-01-01", mapOf("some-key", "some-value")));
        verify(childRepository).updateDetails(caseId, mapOf("some-key", "some-value"));
    }

    @Test
    public void shouldUpdateEveryChildWhileRegistering() throws Exception {
        Child firstChild = new Child("Child X", "Mother X", "female", new HashMap<String, String>());
        Child secondChild = new Child("Child Y", "Mother X", "female", new HashMap<String, String>());
        when(motherRepository.findById("Mother X")).thenReturn(new Mother("Mother X", "EC 1", "TC 1", "2012-01-01"));
        when(childRepository.findByMotherCaseId("Mother X")).thenReturn(asList(firstChild, secondChild));
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("Mother X");

        service.register(submission);

        verify(childRepository).findByMotherCaseId("Mother X");
        verify(childRepository).update(firstChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        verify(childRepository).update(secondChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    public void shouldUpdateEveryChildWhilePNCRegistration() throws Exception {
        Child firstChild = new Child("Child X", "Mother X", "female", new HashMap<String, String>());
        Child secondChild = new Child("Child Y", "Mother X", "female", new HashMap<String, String>());
        when(motherRepository.findAllCasesForEC("EC X")).thenReturn(asList(new Mother("Mother X", "EC X", "TC 1", "2012-01-01")));
        when(childRepository.findByMotherCaseId("Mother X")).thenReturn(asList(firstChild, secondChild));
        FormSubmission submission = mock(FormSubmission.class);
        when(submission.entityId()).thenReturn("EC X");

        service.pncRegistration(submission);

        verify(childRepository).findByMotherCaseId("Mother X");
        verify(childRepository).update(firstChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        verify(childRepository).update(secondChild.setIsClosed(false).setDateOfBirth("2012-01-01").setThayiCardNumber("TC 1"));
        verifyNoMoreInteractions(childRepository);
    }

    @Test
    public void shouldHandleUpdateImmunizationsForChild() throws Exception {
        Action action = ActionBuilder.updateImmunizations("Case X", mapOf("aKey", "aValue"));

        service.updateImmunizations(action);

        verify(allTimelineEvents).add(TimelineEvent.forChildImmunization("Case X", action.get("immunizationsProvided"), action.get("immunizationsProvidedDate")
                , action.get("vitaminADose")));
        verify(childRepository).updateDetails("Case X", mapOf("aKey", "aValue"));
    }

    @Test
    public void shouldCloseChildRecordForDeleteChildAction() throws Exception {
        Action action = ActionBuilder.closeChild("Case X");

        service.delete(action);

        verify(childRepository).close("Case X");
    }
}
