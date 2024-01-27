package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeTestRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var taskRepository: FakeTestRepository

    private lateinit var tasksViewModel: TasksViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        taskRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        taskRepository.addTasks(task1, task2, task3)
        // Given a fresh ViewModel
        tasksViewModel = TasksViewModel(taskRepository)
    }

    @Test
    fun addNewTask_setNewTaskEvent() {
        // When adding a new task
        tasksViewModel.addNewTask()

        // Then the new task event triggered
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled(), not(nullValue()))
    }

    @Test
    fun setFilterAllTasks_tasksAddViewVisible() {

        // When the  filter type is ALL_TASKS
        val type = TasksFilterType.ALL_TASKS
        tasksViewModel.setFiltering(type)
        // Then the Add Task action is visible
        val value = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertThat(value, `is`(true))
    }

    @Test
    fun completeTask_dataAndSnackBarUpdated() {
        // Create an active task and add it to the repository
        val task = Task("Title", "Description")
        taskRepository.addTasks(task)

        // Mark the task as completed task
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed
        assertThat(taskRepository.tasksServiceData[task.id]?.isCompleted, `is`(true))

        // Assert that the snack bar has been updated the correct text
        val snackbarText = tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled(), `is`(R.string.task_marked_complete))
    }
}