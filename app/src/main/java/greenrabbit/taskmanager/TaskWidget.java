package greenrabbit.taskmanager;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.ArrayList;

public class TaskWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.task_widget);
        DataBase dataBase = new DataBase(context);
        dataBase.openDB();
        ArrayList<String> task = dataBase.widgetData();
        dataBase.closeDB();

        views.setTextViewText(R.id.tv_task_w, task.get(0));
        views.setTextViewText(R.id.tv_status_w, task.get(1));

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

