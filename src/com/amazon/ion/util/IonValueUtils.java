/*
 * Copyright 2014-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at:
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.amazon.ion.util;

import com.amazon.ion.IonValue;

/**
 * Utility methods for working with {@link IonValue}s.
 */
public class IonValueUtils
{
    /**
     * Determines whether a value is Java null, or any Ion null.
     *
     * @param value may be null.
     *
     * @return {@code (value == null || value.isNullValue())}
     */
    public static final boolean anyNull(IonValue value)
    {
        return (value == null || value.isNullValue());
    }
}
