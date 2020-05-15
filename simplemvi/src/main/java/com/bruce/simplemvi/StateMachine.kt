package com.bruce.simplemvi

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

class StateMachine<T>(
    initialState: T,
    effectHandler: (e: Effect) -> Observable<out Event>,
    private val reducer: (state: T, e: Event) -> Pair<T, Effect?>
) {
    private val logger: ILogger = Logger()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val events: PublishRelay<Event> = PublishRelay.create()
    private val effects = PublishRelay.create<Effect>()

    private fun newEffect(e: Effect) = effects.accept(e)
    private val effectsHandler: Observable<Event> = effects
        .doOnNext { logger.debug("new effect $it") }
        .switchMap(effectHandler)
    private val viewStates = Observable.merge(events, effectsHandler).distinctUntilChanged()
        .doOnNext { logger.debug("new event $it") }
        .scan(initialState, ::reduce)
        .doOnNext { logger.debug("new state $it") }
        .distinctUntilChanged()
        .replay(1)

    fun viewStateObs(): Observable<T> = viewStates

    fun onUiEvent(event: Event) {
        events.accept(event)
    }

    private fun reduce(state: T, event: Event): T {
        val (newState, effect) = reducer(state, event)
        effect?.let { newEffect(it) }
        return newState
    }

    fun start() {
        compositeDisposable += viewStates.connect()
    }

    fun stop() = compositeDisposable.clear()
}