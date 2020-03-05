package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.preferences.editor.EditorActivity;
import org.nexttracks.android.ui.preferences.editor.EditorMvvm;
import org.nexttracks.android.ui.preferences.editor.EditorViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseActivityModule.class)
public abstract class EditorActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(EditorActivity a);

    @Binds abstract EditorMvvm.ViewModel bindViewModel(EditorViewModel viewModel);
}
