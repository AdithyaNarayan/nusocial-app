package com.teamnusocial.nusocial.ui.broadcast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.Timestamp
import com.teamnusocial.nusocial.data.model.Gender
import com.teamnusocial.nusocial.data.model.LocationLatLng
import com.teamnusocial.nusocial.data.model.Module
import com.teamnusocial.nusocial.data.model.User
import com.teamnusocial.nusocial.data.repository.AuthUserRepository
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.auth.AuthViewModel
import com.teamnusocial.nusocial.ui.broadcast.BroadcastViewModel
import com.teamnusocial.nusocial.utils.TestCoroutineRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BroadcastViewModelTest : TestCase() {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: BroadcastViewModel

    @Mock
    private lateinit var userRepository: UserRepository

    @Before
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = BroadcastViewModel(userRepository)
    }

    @Test
    fun getUsersTest() {
        // Given
        val expectedResult = mutableListOf(
            User(),
            User(
                "test",
                "test",
                Gender.MALE,
                "",
                listOf(),
                3,
                "",
                LocationLatLng(1.0, 1.0, ""),
                listOf("test1"),
                "",
                Timestamp.now(),
                mutableListOf()
            )
        )
        runBlocking {
            // When
            `when`(userRepository.getUsers()).thenReturn(expectedResult)

            // Then
            assertEquals(expectedResult, viewModel.getUsers())
        }
    }

//    @Test
//    fun sendBroadcastTest() {
//        // Given
//        val currUserID = "testUserID"
//        val expectedResult = mutableListOf(
//            User(),
//            User(
//                "test",
//                "test",
//                Gender.MALE,
//                "",
//                listOf(),
//                3,
//                "",
//                LocationLatLng(1.0, 1.0, ""),
//                listOf("test1"),
//                "",
//                Timestamp.now(),
//                mutableListOf()
//            )
//        )
//        runBlocking {
//            // When
//            `when`(userRepository.getUserAnd(currUserID, ArgumentMatchers.any())).then {
//                it.getArgument(1).
//            }
//            `when`(userRepository.getUsers()).thenReturn(expectedResult)
//
//            // Then
//            assertEquals(expectedResult, viewModel.getUsers())
//        }
//    }
}