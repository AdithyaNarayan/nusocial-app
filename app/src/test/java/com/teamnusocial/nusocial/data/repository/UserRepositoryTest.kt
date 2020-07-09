package com.teamnusocial.nusocial.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.ui.auth.AuthViewModel
import com.teamnusocial.nusocial.utils.FirestoreUtils
import com.teamnusocial.nusocial.utils.TestCoroutineRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserRepositoryTest : TestCase() {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var firestoreUtils: FirestoreUtils

    @Mock
    private lateinit var taskQuerySnapshot: Task<QuerySnapshot>

    @Before
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        userRepository = UserRepository(firestoreUtils)
        firestoreUtils = spy(FirestoreUtils())
    }

    /*@Test
    suspend fun getUsersTest() {
        // Test 1
        // Given
        val expectedResult = User()

        // When
        `when`(firestoreUtils.getAllUsers().get()).thenReturn(taskQuerySnapshot)

        doAnswer {
            
        }

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ValueEventListener valueEventListener = (ValueEventListener) invocation.getArguments()[0];

                DataSnapshot mockedDataSnapshot = Mockito.mock(DataSnapshot.class);
                //when(mockedDataSnapshot.getValue(User.class)).thenReturn(testOrMockedUser)

                valueEventListener.onDataChange(mockedDataSnapshot);
                //valueEventListener.onCancelled(...);

                return null;
            }
        }).when(mockedDatabaseReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        new LoginActivity().getSignedInUserProfile();

        // check preferences are updated

    }*/
}