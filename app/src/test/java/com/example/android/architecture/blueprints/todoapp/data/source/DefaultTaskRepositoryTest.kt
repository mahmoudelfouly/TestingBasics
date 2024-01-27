package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DefaultTaskRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTask = listOf(task1, task2).sortedBy { it.id }
    private val localTask = listOf(task3).sortedBy { it.id }
    private val newTask = listOf(task3).sortedBy { it.id }

    private lateinit var taskRemoteDataSource: FakeDataSource
    private lateinit var taskLocalDataSource: FakeDataSource

    private lateinit var tasksRepository: DefaultTasksRepository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun createRepository() {
        taskRemoteDataSource = FakeDataSource(remoteTask.toMutableList())
        taskLocalDataSource = FakeDataSource(localTask.toMutableList())

        tasksRepository = DefaultTasksRepository(
            taskRemoteDataSource,
            taskLocalDataSource,
            Dispatchers.Main
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        // When tasks are requested from the task repository
        val tasks = tasksRepository.getTasks(true) as Result.Success
        // Then tasks are loaded from remote data source
        assertThat(tasks.data, IsEqual(remoteTask))
    }
}