package com.teamnusocial.nusocial.ui.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.teamnusocial.nusocial.data.repository.AuthUserRepository
import com.teamnusocial.nusocial.utils.TestCoroutineRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest : TestCase() {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: AuthViewModel

    @Mock
    private lateinit var authUserRepository: AuthUserRepository


    @Before
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = AuthViewModel(authUserRepository)
    }

    @Test
    fun validEmailTest() {
        // Test 1
        // Given
        var email = "abc@gmail.com"
        var expectedResult = true

        // When
        viewModel.updateValidEmail(email)

        // Then
        assertEquals(expectedResult, viewModel.isValidEmail.value)

        // Test 2
        // Given
        email = "abc"
        expectedResult = false

        // When
        viewModel.updateValidEmail(email)

        // Then
        assertEquals(expectedResult, viewModel.isValidEmail.value)
    }

    @Test
    fun validPasswordTest() {
        // Test 1
        // Given
        var password = "somecomplexpassword"
        var expectedResult = true

        // When
        viewModel.updateValidPassword(password)

        // Then
        assertEquals(expectedResult, viewModel.isValidPassword.value)

        // Test 2
        // Given
        password = "1234"
        expectedResult = false

        // When
        viewModel.updateValidPassword(password)

        // Then
        assertEquals(expectedResult, viewModel.isValidPassword.value)
    }

    @Test
    fun signedInTest() {
        // Test 1
        // Given
        var expectedResult = true

        // When
        `when`(authUserRepository.isSignedIn()).thenReturn(true)

        // Then
        assertEquals(expectedResult, viewModel.isSignedIn().value)

        // Test 2
        // Given
        expectedResult = false

        // When
        `when`(authUserRepository.isSignedIn()).thenReturn(false)

        // Then
        assertEquals(expectedResult, viewModel.isSignedIn().value)

    }
}