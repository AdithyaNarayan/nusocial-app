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
        //Given
        val email = "abc@gmail.com"
        val expectedResult = true

        //When
        viewModel.updateValidEmail(email)

        //Then
        assertEquals(expectedResult, viewModel.isValidEmail.value)
    }

}