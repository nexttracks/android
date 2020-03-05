package org.nexttracks.android.injection.modules.android.ActivityModules;

import androidx.appcompat.app.AppCompatActivity;

import org.nexttracks.android.injection.scopes.PerActivity;
import org.nexttracks.android.ui.contacts.ContactsActivity;
import org.nexttracks.android.ui.contacts.ContactsMvvm;
import org.nexttracks.android.ui.contacts.ContactsViewModel;

import dagger.Binds;
import dagger.Module;

@Module(includes = BaseActivityModule.class)
public abstract class ContactsActivityModule {

    @Binds
    @PerActivity
    abstract AppCompatActivity bindActivity(ContactsActivity a);

    @Binds abstract ContactsMvvm.ViewModel bindViewModel(ContactsViewModel viewModel);
}