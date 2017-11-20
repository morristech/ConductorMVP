package com.jshvarts.conductormvp.addnote

import com.jshvarts.conductormvp.domain.NoteRepository
import com.jshvarts.conductormvp.domain.model.Note
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class AddNotePresenter @Inject constructor(private val repository: NoteRepository) : AddNoteContract.Presenter {

    private lateinit var view: AddNoteView

    private val disposables: CompositeDisposable = CompositeDisposable()

    override fun detachView() {
        Timber.d("NotesPresenter::detachView")
        disposables.clear()
    }

    override fun attachView(view: AddNoteView) {
        Timber.d("NotesPresenter::attachView")
        this.view = view
    }

    override fun addNote(noteText: String) {
        Note(noteText = noteText).apply {
            if (!isValid()) {
                view.onNoteValidationFailed()
                return
            }
            disposables.add(repository.update(this)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::onNoteAddSuccess, { e -> showUnableToAddNoteError(e) } ))
        }
    }

    private fun showUnableToAddNoteError(error: Throwable) {
        Timber.e(error)
        view.onNoteAddFailed()
    }
}