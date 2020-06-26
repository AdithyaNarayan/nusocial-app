package com.teamnusocial.nusocial.ui.buddymatch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.teamnusocial.nusocial.data.repository.UserRepository
import com.teamnusocial.nusocial.ui.broadcast.BroadcastViewModel
import com.teamnusocial.nusocial.utils.TestCoroutineRule
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi

import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BuddyMatchViewModelTest : TestCase() {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private lateinit var viewModel: BuddyMatchViewModel

    @Mock
    private lateinit var userRepository: UserRepository

    @Before
    public override fun setUp() {
        MockitoAnnotations.initMocks(this)
        viewModel = BuddyMatchViewModel()
    }
}