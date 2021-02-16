package com.brackeys.ui.data.repository.themes

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.brackeys.ui.data.converter.ThemeConverter
import com.brackeys.ui.data.database.AppDatabase
import com.brackeys.ui.data.database.entity.theme.ThemeEntity
import com.brackeys.ui.data.model.theme.ExternalTheme
import com.brackeys.ui.data.settings.SettingsManager
import com.brackeys.ui.domain.model.theme.Meta
import com.brackeys.ui.domain.model.theme.Property
import com.brackeys.ui.domain.model.theme.PropertyItem
import com.brackeys.ui.domain.model.theme.ThemeModel
import com.brackeys.ui.domain.providers.coroutine.DispatcherProvider
import com.brackeys.ui.domain.repository.themes.ThemesRepository
import com.brackeys.ui.filesystem.base.Filesystem
import com.brackeys.ui.filesystem.base.model.FileModel
import com.brackeys.ui.filesystem.base.model.FileParams
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File

class ThemesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val settingsManager: SettingsManager,
    private val appDatabase: AppDatabase,
    private val filesystem: Filesystem,
    private val context: Context
) : ThemesRepository {

    companion object {
        private const val FALLBACK_COLOR = "#000000"
    }

    // region PROPERTIES

    private var textColor: String = FALLBACK_COLOR
    private var backgroundColor: String = FALLBACK_COLOR
    private var gutterColor: String = FALLBACK_COLOR
    private var gutterDividerColor: String = FALLBACK_COLOR
    private var gutterCurrentLineNumberColor: String = FALLBACK_COLOR
    private var gutterTextColor: String = FALLBACK_COLOR
    private var selectedLineColor: String = FALLBACK_COLOR
    private var selectionColor: String = FALLBACK_COLOR
    private var suggestionQueryColor: String = FALLBACK_COLOR
    private var findResultBackgroundColor: String = FALLBACK_COLOR
    private var delimiterBackgroundColor: String = FALLBACK_COLOR
    private var numberColor: String = FALLBACK_COLOR
    private var operatorColor: String = FALLBACK_COLOR
    private var keywordColor: String = FALLBACK_COLOR
    private var typeColor: String = FALLBACK_COLOR
    private var langConstColor: String = FALLBACK_COLOR
    private var preprocessorColor: String = FALLBACK_COLOR
    private var methodColor: String = FALLBACK_COLOR
    private var stringColor: String = FALLBACK_COLOR
    private var commentColor: String = FALLBACK_COLOR
    private var tagColor: String = FALLBACK_COLOR
    private var tagNameColor: String = FALLBACK_COLOR
    private var attrNameColor: String = FALLBACK_COLOR
    private var attrValueColor: String = FALLBACK_COLOR
    private var entityRefColor: String = FALLBACK_COLOR

    // endregion PROPERTIES

    override suspend fun fetchThemes(searchQuery: String): List<ThemeModel> {
        return withContext(dispatcherProvider.io()) {
            appDatabase.themeDao()
                .loadAll(searchQuery)
                .map(ThemeConverter::toModel)
        }
    }

    override suspend fun fetchTheme(uuid: String): ThemeModel {
        return withContext(dispatcherProvider.io()) {
            val themeEntity = appDatabase.themeDao().load(uuid)
            ThemeConverter.toModel(themeEntity)
        }
    }

    override suspend fun importTheme(uri: Uri): ThemeModel {
        return withContext(dispatcherProvider.io()) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val themeJson = inputStream?.bufferedReader()?.use(BufferedReader::readText)!!
            val externalTheme = ExternalTheme.deserialize(themeJson)
            ThemeConverter.toModel(externalTheme)
        }
    }

    override suspend fun exportTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            val externalTheme = ThemeConverter.toExternalTheme(themeModel)
            val fileName = "${themeModel.name}.json"
            val fileText = ExternalTheme.serialize(externalTheme)
            val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val fileModel = FileModel(
                name = fileName,
                path = File(directory, fileName).absolutePath,
                size = 0L,
                lastModified = 0L,
                isFolder = false,
                isHidden = false
            )
            filesystem.saveFile(fileModel, fileText, FileParams())
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe()
        }
    }

    override suspend fun createTheme(meta: Meta, properties: List<PropertyItem>) {
        return withContext(dispatcherProvider.io()) {
            for (property in properties) {
                when (property.propertyKey) {
                    Property.TEXT_COLOR -> textColor = property.propertyValue
                    Property.BACKGROUND_COLOR -> backgroundColor = property.propertyValue
                    Property.GUTTER_COLOR -> gutterColor = property.propertyValue
                    Property.GUTTER_DIVIDER_COLOR -> gutterDividerColor = property.propertyValue
                    Property.GUTTER_CURRENT_LINE_NUMBER_COLOR -> gutterCurrentLineNumberColor = property.propertyValue
                    Property.GUTTER_TEXT_COLOR -> gutterTextColor = property.propertyValue
                    Property.SELECTED_LINE_COLOR -> selectedLineColor = property.propertyValue
                    Property.SELECTION_COLOR -> selectionColor = property.propertyValue
                    Property.SUGGESTION_QUERY_COLOR -> suggestionQueryColor = property.propertyValue
                    Property.FIND_RESULT_BACKGROUND_COLOR -> findResultBackgroundColor = property.propertyValue
                    Property.DELIMITER_BACKGROUND_COLOR -> delimiterBackgroundColor = property.propertyValue
                    Property.NUMBER_COLOR -> numberColor = property.propertyValue
                    Property.OPERATOR_COLOR -> operatorColor = property.propertyValue
                    Property.KEYWORD_COLOR -> keywordColor = property.propertyValue
                    Property.TYPE_COLOR -> typeColor = property.propertyValue
                    Property.LANG_CONST_COLOR -> langConstColor = property.propertyValue
                    Property.PREPROCESSOR_COLOR -> preprocessorColor = property.propertyValue
                    Property.METHOD_COLOR -> methodColor = property.propertyValue
                    Property.STRING_COLOR -> stringColor = property.propertyValue
                    Property.COMMENT_COLOR -> commentColor = property.propertyValue
                    Property.TAG_COLOR -> tagColor = property.propertyValue
                    Property.TAG_NAME_COLOR -> tagNameColor = property.propertyValue
                    Property.ATTR_NAME_COLOR -> attrNameColor = property.propertyValue
                    Property.ATTR_VALUE_COLOR -> attrValueColor = property.propertyValue
                    Property.ENTITY_REF_COLOR -> entityRefColor = property.propertyValue
                }
            }
            val themeEntity = ThemeEntity(
                uuid = meta.uuid,
                name = meta.name,
                author = meta.author,
                description = meta.description,
                textColor = textColor,
                backgroundColor = backgroundColor,
                gutterColor = gutterColor,
                gutterDividerColor = gutterDividerColor,
                gutterCurrentLineNumberColor = gutterCurrentLineNumberColor,
                gutterTextColor = gutterTextColor,
                selectedLineColor = selectedLineColor,
                selectionColor = selectionColor,
                suggestionQueryColor = suggestionQueryColor,
                findResultBackgroundColor = findResultBackgroundColor,
                delimiterBackgroundColor = delimiterBackgroundColor,
                numberColor = numberColor,
                operatorColor = operatorColor,
                keywordColor = keywordColor,
                typeColor = typeColor,
                langConstColor = langConstColor,
                preprocessorColor = preprocessorColor,
                methodColor = methodColor,
                stringColor = stringColor,
                commentColor = commentColor,
                tagColor = tagColor,
                tagNameColor = tagNameColor,
                attrNameColor = attrNameColor,
                attrValueColor = attrValueColor,
                entityRefColor = entityRefColor
            )

            appDatabase.themeDao().insert(themeEntity)

            textColor = FALLBACK_COLOR
            backgroundColor = FALLBACK_COLOR
            gutterColor = FALLBACK_COLOR
            gutterDividerColor = FALLBACK_COLOR
            gutterCurrentLineNumberColor = FALLBACK_COLOR
            gutterTextColor = FALLBACK_COLOR
            selectedLineColor = FALLBACK_COLOR
            selectionColor = FALLBACK_COLOR
            suggestionQueryColor = FALLBACK_COLOR
            findResultBackgroundColor = FALLBACK_COLOR
            delimiterBackgroundColor = FALLBACK_COLOR
            numberColor = FALLBACK_COLOR
            operatorColor = FALLBACK_COLOR
            keywordColor = FALLBACK_COLOR
            typeColor = FALLBACK_COLOR
            langConstColor = FALLBACK_COLOR
            methodColor = FALLBACK_COLOR
            stringColor = FALLBACK_COLOR
            commentColor = FALLBACK_COLOR
            tagColor = FALLBACK_COLOR
            tagNameColor = FALLBACK_COLOR
            attrNameColor = FALLBACK_COLOR
            attrValueColor = FALLBACK_COLOR
            entityRefColor = FALLBACK_COLOR
        }
    }

    override suspend fun removeTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            appDatabase.themeDao().delete(ThemeConverter.toEntity(themeModel))
            if (settingsManager.getColorScheme().get() == themeModel.uuid) {
                settingsManager.getColorScheme().delete()
            }
        }
    }

    override suspend fun selectTheme(themeModel: ThemeModel) {
        withContext(dispatcherProvider.io()) {
            settingsManager.getColorScheme().set(themeModel.uuid)
        }
    }
}