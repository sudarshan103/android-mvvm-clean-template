package com.arch.mvvm.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arch.domain.model.Result
import com.arch.domain.model.TimeZone
import com.arch.mvvm.ui.TimeZoneViewModel
import com.arch.mvvm.ui.ext.TimezoneDetector
import com.arch.mvvm.ui.ext.compareWithLocalTime
import com.arch.mvvm.ui.ext.formatToDisplayDate
import com.arch.mvvm.ui.ext.getAbbreviation
import com.arch.mvvm.ui.ext.getUtcOffsetString

/**
 * Main screen composable for displaying timezone information.
 * Follows single responsibility principle - only handles UI rendering.
 */
@Composable
fun TimeZoneScreen(
    viewModel: TimeZoneViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val detectedTimezone = TimezoneDetector.detectDeviceTimezone(context)

    val utcTimeState by viewModel.utcTimeState.collectAsState()
    val timeZoneState by viewModel.timeZoneState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScreenTitle()

        TimeZoneCard(
            title = "UTC Time",
            result = utcTimeState,
            onRetry = { viewModel.fetchTimeZone(TimeZoneViewModel.UTC) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimeZoneCard(
            title = "Device Timezone ($detectedTimezone)",
            result = timeZoneState,
            onRetry = { viewModel.fetchTimeZone(detectedTimezone) }
        )

        TimeComparisonCard(timeZoneState)

        Spacer(modifier = Modifier.height(24.dp))

        ActionButtons(
            onFetchUtc = { viewModel.fetchTimeZone(TimeZoneViewModel.UTC) },
            onFetchDeviceTimezone = { viewModel.fetchTimeZone(detectedTimezone) },
            onClearErrors = { viewModel.clearError() }
        )
    }
}

@Composable
private fun ScreenTitle() {
    Text(
        text = "TimeZone API Demo",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
private fun TimeComparisonCard(timeZoneState: Result<TimeZone>?) {
    if (timeZoneState is Result.Success) {
        val timeZone = timeZoneState.data
        val (comparison, timesMatch) = timeZone.compareWithLocalTime()

        Spacer(modifier = Modifier.height(16.dp))

        val backgroundColor = if (timesMatch) {
            Color(0xFF4CAF50)
        } else {
            Color(0xFFF44336)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Text(
                text = "Device Comparison:\n$comparison",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onFetchUtc: () -> Unit,
    onFetchDeviceTimezone: () -> Unit,
    onClearErrors: () -> Unit
) {
    Button(
        onClick = onFetchUtc,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text("Fetch UTC Time")
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = onFetchDeviceTimezone,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text("Fetch Device Timezone")
    }

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = onClearErrors,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text("Clear Errors")
    }
}

@Composable
fun TimeZoneCard(
    title: String,
    result: Result<TimeZone>?,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            when (result) {
                is Result.Success -> TimeZoneSuccessContent(result.data)
                is Result.Error -> ErrorContent(
                    message = result.exception.message ?: "Unknown error",
                    onRetry = onRetry
                )
                is Result.Loading -> LoadingContent()
                null -> EmptyContent()
            }
        }
    }
}

@Composable
private fun TimeZoneSuccessContent(timeZone: TimeZone) {
    Text(
        text = "Time: ${timeZone.formatToDisplayDate()}",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Timezone: ${timeZone.timezone}",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Text(
        text = "Abbreviation: ${timeZone.getAbbreviation()}",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
    Text(
        text = "UTC Offset: ${timeZone.getUtcOffsetString()}",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Text(
        text = "Error: $message",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Button(
        onClick = onRetry,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text("Retry")
    }
}

@Composable
private fun LoadingContent() {
    Text(
        text = "Loading...",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

@Composable
private fun EmptyContent() {
    Text(
        text = "No data loaded yet",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}

