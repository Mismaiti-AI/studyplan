package com.mytask.di

import com.mytask.data.repositories.appconfig.AppConfigRepository
import com.mytask.data.repositories.appconfig.AppConfigRepositoryImpl
import com.mytask.data.repositories.assignment.AssignmentRepository
import com.mytask.data.repositories.assignment.AssignmentRepositoryImpl
import com.mytask.data.repositories.exam.ExamRepository
import com.mytask.data.repositories.exam.ExamRepositoryImpl
import com.mytask.data.repositories.project.ProjectRepository
import com.mytask.data.repositories.project.ProjectRepositoryImpl
import com.mytask.domain.usecase.GetAssignmentListUseCase
import com.mytask.domain.usecase.GetCurrentSheetConfigUseCase
import com.mytask.domain.usecase.GetDashboardOverviewUseCase
import com.mytask.domain.usecase.GetExamListUseCase
import com.mytask.domain.usecase.GetProjectListUseCase
import com.mytask.domain.usecase.GetUpcomingItemsUseCase
import com.mytask.domain.usecase.MarkAssignmentCompleteUseCase
import com.mytask.domain.usecase.UpdateGoogleSheetUrlUseCase
import com.mytask.domain.usecase.ValidateSheetUrlUseCase
import com.mytask.domain.usecase.ViewAssignmentDetailsUseCase
import com.mytask.domain.usecase.ViewExamDetailsUseCase
import com.mytask.domain.usecase.ViewProjectDetailsUseCase
import com.mytask.presentation.assignmentdetail.AssignmentDetailViewModel
import com.mytask.presentation.assignmentlist.AssignmentListViewModel
import com.mytask.presentation.dashboard.DashboardViewModel
import com.mytask.presentation.examdetail.ExamDetailViewModel
import com.mytask.presentation.examlist.ExamListViewModel
import com.mytask.presentation.projectdetail.ProjectDetailViewModel
import com.mytask.presentation.projectlist.ProjectListViewModel
import com.mytask.presentation.settings.SettingsViewModel
import com.mytask.presentation.sheeturlconfig.SheetUrlConfigViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // Repositories
    singleOf(::AssignmentRepositoryImpl) bind AssignmentRepository::class
    singleOf(::ExamRepositoryImpl) bind ExamRepository::class
    singleOf(::ProjectRepositoryImpl) bind ProjectRepository::class
    singleOf(::AppConfigRepositoryImpl) bind AppConfigRepository::class

    // Use Cases
    factoryOf(::GetAssignmentListUseCase)
    factoryOf(::MarkAssignmentCompleteUseCase)
    factoryOf(::ViewAssignmentDetailsUseCase)
    factoryOf(::GetExamListUseCase)
    factoryOf(::ViewExamDetailsUseCase)
    factoryOf(::GetProjectListUseCase)
    factoryOf(::ViewProjectDetailsUseCase)
    factoryOf(::GetDashboardOverviewUseCase)
    factoryOf(::GetUpcomingItemsUseCase)
    factoryOf(::UpdateGoogleSheetUrlUseCase)
    factoryOf(::ValidateSheetUrlUseCase)
    factoryOf(::GetCurrentSheetConfigUseCase)

    // ViewModels
    viewModelOf(::AssignmentListViewModel)
    viewModelOf(::AssignmentDetailViewModel)
    viewModelOf(::ExamListViewModel)
    viewModelOf(::ExamDetailViewModel)
    viewModelOf(::ProjectListViewModel)
    viewModelOf(::ProjectDetailViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SheetUrlConfigViewModel)
}

// Register ViewModels with explicit function since viewModelOf might not work for all cases
val viewModelModule = module {
    viewModel { AssignmentListViewModel(get()) }
    viewModel { AssignmentDetailViewModel(get()) }
    viewModel { ExamListViewModel(get()) }
    viewModel { ExamDetailViewModel(get()) }
    viewModel { ProjectListViewModel(get()) }
    viewModel { ProjectDetailViewModel(get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { SheetUrlConfigViewModel(get(), get()) }
}