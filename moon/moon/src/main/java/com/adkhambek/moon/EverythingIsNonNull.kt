/*
 * Copyright (C) 2018 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adkhambek.moon

import java.lang.annotation.ElementType
import javax.annotation.Nonnull
import javax.annotation.meta.TypeQualifierDefault

/**
 * Extends `ParametersAreNonnullByDefault` to also apply to Method results and fields.
 * @see javax.annotation.ParametersAreNonnullByDefault
 */
@Nonnull
@MustBeDocumented
@TypeQualifierDefault(ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
public annotation class EverythingIsNonNull
