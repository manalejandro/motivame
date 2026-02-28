package com.manalejandro.motivame.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.manalejandro.motivame.MainActivity
import com.manalejandro.motivame.R
import com.manalejandro.motivame.data.Task
import com.manalejandro.motivame.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MotivameWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { updateWidget(context, appWidgetManager, it) }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        updateWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH) {
            val mgr = AppWidgetManager.getInstance(context)
            mgr.getAppWidgetIds(ComponentName(context, MotivameWidget::class.java))
                .forEach { updateWidget(context, mgr, it) }
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.manalejandro.motivame.WIDGET_REFRESH"

        // ── Número de tareas según altura real (MIN_HEIGHT) ─────────────
        // ~1 fila ≈ 74dp, ~2 filas ≈ 148dp, ~3 filas ≈ 222dp, ~4 filas ≈ 296dp
        private fun taskCount(heightDp: Int) = when {
            heightDp >= 220 -> 3
            heightDp >= 145 -> 2
            else            -> 1
        }

        // ── Número de metas por tarea según espacio disponible ───────────
        // Se divide la altura disponible entre el número de tareas para estimar
        // el espacio por tarea y decidir cuántas metas caben.
        private fun goalsPerTask(heightDp: Int, tasks: Int): Int {
            val spacePerTask = heightDp / tasks
            return when {
                spacePerTask >= 160 -> 3   // mucho espacio → 3 metas
                spacePerTask >= 100 -> 2   // espacio medio → 2 metas
                else                -> 1   // poco espacio  → 1 meta
            }
        }

        private fun layoutFor(tasks: Int) = when (tasks) {
            3    -> R.layout.widget_motivame_large
            2    -> R.layout.widget_motivame_medium
            else -> R.layout.widget_motivame_small
        }

        // IDs agrupados por tarea y slot de meta
        private val TITLE_IDS = intArrayOf(
            R.id.widget_task_title, R.id.widget_task2_title, R.id.widget_task3_title
        )
        private val GOAL_IDS = arrayOf(
            intArrayOf(R.id.widget_t1_goal1, R.id.widget_t1_goal2, R.id.widget_t1_goal3),
            intArrayOf(R.id.widget_t2_goal1, R.id.widget_t2_goal2, R.id.widget_t2_goal3),
            intArrayOf(R.id.widget_t3_goal1, R.id.widget_t3_goal2, R.id.widget_t3_goal3)
        )
        private val CHIP_IDS = intArrayOf(
            R.id.widget_chip1, R.id.widget_chip2, R.id.widget_chip3
        )

        fun updateWidget(context: Context, mgr: AppWidgetManager, widgetId: Int) {
            CoroutineScope(Dispatchers.IO).launch {
                val options   = mgr.getAppWidgetOptions(widgetId)
                // MIN_HEIGHT = altura real actual del widget en el launcher
                val heightDp  = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 110)
                val numTasks = taskCount(heightDp)
                val numGoals = goalsPerTask(heightDp, numTasks)
                val showChip = numTasks == 3   // chip solo en layout grande

                val activeTasks = TaskRepository(context).tasks.first().filter { it.isActive }
                val views = RemoteViews(context.packageName, layoutFor(numTasks))

                // Click → abrir app
                views.setOnClickPendingIntent(
                    R.id.widget_root,
                    PendingIntent.getActivity(
                        context, widgetId,
                        Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        },
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                views.setTextViewText(R.id.widget_hint,
                    context.getString(R.string.widget_tap_to_open))

                if (activeTasks.isEmpty()) {
                    views.setTextViewText(R.id.widget_status, "○")
                    views.setTextViewText(R.id.widget_task_title, "Motívame")
                    views.setTextViewText(R.id.widget_t1_goal1,
                        context.getString(R.string.widget_no_tasks))
                    // ocultar metas 2 y 3 del slot 1
                    views.setViewVisibility(R.id.widget_t1_goal2, View.GONE)
                    views.setViewVisibility(R.id.widget_t1_goal3, View.GONE)
                    // ocultar slots extra
                    if (numTasks >= 2) hideTaskSlot(views, 1, showChip)
                    if (numTasks >= 3) hideTaskSlot(views, 2, showChip)
                } else {
                    views.setTextViewText(R.id.widget_status, "●")
                    for (slot in 0 until numTasks) {
                        val task = activeTasks.getOrNull(slot)
                        fillTaskSlot(context, views, slot, task, numGoals, showChip)
                    }
                }

                mgr.updateAppWidget(widgetId, views)
            }
        }

        private fun fillTaskSlot(
            context: Context,
            views: RemoteViews,
            slot: Int,        // 0-based
            task: Task?,
            numGoals: Int,
            showChip: Boolean
        ) {
            val titleId = TITLE_IDS[slot]
            val goalIds = GOAL_IDS[slot]

            if (task == null) {
                hideTaskSlot(views, slot, showChip)
                return
            }

            views.setViewVisibility(titleId, View.VISIBLE)
            views.setTextViewText(titleId, task.title)

            // Rellenar metas con opacidad decreciente; ocultar las que excedan numGoals
            val goals = task.goals
            for (i in 0..2) {
                val goalId = goalIds[i]
                if (i < numGoals && i < goals.size) {
                    views.setViewVisibility(goalId, View.VISIBLE)
                    views.setTextViewText(goalId, "🎯 ${goals[i]}")
                } else {
                    views.setViewVisibility(goalId, View.GONE)
                }
            }

            // Chip de avisos (solo layout grande)
            if (showChip) {
                val chipId = CHIP_IDS[slot]
                val chipText = if (task.repeatEveryDays == 1)
                    context.getString(R.string.task_summary_reminders_daily, task.dailyReminders)
                else
                    context.getString(R.string.task_summary_reminders, task.dailyReminders, task.repeatEveryDays)
                views.setViewVisibility(chipId, View.VISIBLE)
                views.setTextViewText(chipId, "🔔 $chipText")
            }
        }

        private fun hideTaskSlot(views: RemoteViews, slot: Int, showChip: Boolean) {
            views.setViewVisibility(TITLE_IDS[slot], View.GONE)
            GOAL_IDS[slot].forEach { views.setViewVisibility(it, View.GONE) }
            if (showChip) views.setViewVisibility(CHIP_IDS[slot], View.GONE)
        }

        fun requestUpdate(context: Context) {
            context.sendBroadcast(
                Intent(context, MotivameWidget::class.java).apply {
                    action = ACTION_REFRESH
                }
            )
        }
    }
}

