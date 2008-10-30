/*
 * Copyright (c) 2008 Amazon.com, Inc.  All rights reserved.
 */

package com.amazon.ion;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Writes Ion data to an output source.
 *
 * This interface allows
 * the user to logically write the values as they view the data
 * without being concerned about which output format is needed.
 * <p>
 * A value is written via the set of typed {@code write*()} methods such as
 * {@link #writeBool(boolean)} and {@link #writeInt(long)}.
 * Each of these methods outputs a single Ion value, and afterwards the writer
 * is prepared to receive the data for the next sibling value.
 * <p>
 * Any type annotations must be set before the value is written.
 * Once the value has been written the "pending annotations" are erased,
 * so they are must be set again if they need to be applied to the next value.
 * <p>
 * Similarly the field name must be set before the value is
 * written (assuming the value is a field in a structure).  The field name
 * is also "erased" once used, so it must be set for each field.
 * <p>
 * To write a container, first call the appropriate {@code open*()} method,
 * for example, {@link #openList()}. This write each child value in order.
 * Finally, call the matching {@code close*()} method, for example,
 * {@link #closeList()}).
 * <p>
 * Several {@code write*List()}
 * helper routines are included to simplify writing arrays of
 * common scalars to the output.  For binary this output can be
 * optimized for some performance gain, but for text and tree
 * these are simply convenience covers.
 * <p>
 * Once all the values have been written into the writer the
 * caller can use getBytes() or writeBytes() to get the cached output
 * as a byte array (either a new one allocated by the writer or
 * a user supplied buffer), or output to an output stream.
 */
public interface IonWriter
{

    /**
     * sets the symbol table to use for encoding to be the passed
     * in symbol table.
     * @param symbols base symbol table for encoding
     */
    public abstract void setSymbolTable(SymbolTable symbols);

    /**
     * returns the symbol table that is currently in use by the writer.
     * At the end of writing a number of values this table will be
     * populated with any added symbols.
     * <p>
     * Note that the table may be replaced during processing.  For example,
     * the stream may start out with a system table that's later replaced by a
     * local table in order to store newly-encountered symbols.
     *
     * @return current symbol table
     */
    public abstract SymbolTable getSymbolTable();


    /**
     * Sets the pending field name to the given symbol id.
     * The id is expected to be already present in the current symbol table
     * (but this is not checked).
     * <p>
     * The pending field name is cleared when the current value is written via
     * one of the {@code write*()} or {@code open*()} methods.
     *
     * @param id symbol id of the field name
     *
     * @throws IllegalStateException if the current container isn't a struct,
     * that is, if {@link #isInStruct()} is false.
     */
    public abstract void setFieldId(int id);

    /**
     * Sets the pending field name to the given text.
     * If the string is not present in the current symbol table,
     * it will be added.
     * <p>
     * The pending field name is cleared when the current value is written via
     * one of the {@code write*()} or {@code open*()} methods.
     *
     * @param name text of the field name
     *
     * @throws IllegalStateException if the current container isn't a struct,
     * that is, if {@link #isInStruct()} is false.
     */
    public abstract void setFieldName(String name);


    /**
     * Sets the full list of pending annotations to the given text symbols.
     * Any pending annotations are cleared.
     * The contents of the {@code annotations} array are copied into this
     * writer, so the caller does not need to preserve the array.
     * <p>
     * The list of pending annotations is cleared when the current value is
     * written via one of the {@code write*()} or {@code open*()} methods.
     *
     * @param annotations string array with the annotations
     */
    public abstract void setTypeAnnotations(String[] annotations);

    /**
     * Sets the full list of pending annotations to the given symbol ids.
     * Any pending annotations are cleared.
     * The contents of the {@code annotations} array are copied into this
     * writer, so the caller does not need to preserve the array.
     * <p>
     * The list of pending annotations is cleared when the current value is
     * written via one of the {@code write*()} or {@code open*()} methods.
     *
     * @param annotationIds array with the annotation symbol ids
     */
    public abstract void setTypeAnnotationIds(int[] annotationIds);

    /**
     * Adds a given string to the list of pending annotations.
     * <p>
     * The list of pending annotations is cleared when the current value is
     * written via one of the {@code write*()} or {@code open*()} methods.
     *
     * @param annotation string annotation to append to the annotation list
     */
    public abstract void addTypeAnnotation(String annotation);

    /**
     * Adds a given symbol id to the list of pending annotations.
     * <p>
     * The list of pending annotations is cleared when the current value is
     * written via one of the {@code write*()} or {@code open*()} methods.
     *
     * @param annotationId symbol id to append to the annotation list
     */
    public abstract void addTypeAnnotationId(int annotationId);


    //=========================================================================
    // Value writing

    /**
     * writes the contents of the passed in Ion value to the output.
     * This also writes the values annotations and the values field
     * names (if it is in a structure) along with the values contents.
     * This does a deep write, which writes the contents of any
     * containers encountered.
     */
    public abstract void writeIonValue(IonValue value) throws IOException;

    /**
     * writes the current value this iterator is positioned on as a
     * value of type t to the writers output.
     * This also writes the values annotations and the values field
     * names (if it is in a structure) along with the values contents.
     * This does a deep write, which writes the contents of any
     * containers encountered.
     */
    public abstract void writeIonValue(IonType t, IonReader iterator)
        throws IOException;

    /**
     * Writes the remaining contents of the given reader to the
     * output. This also writes the values annotations and the values
     * field names (if it is in a structure) along with the values
     * contents. This does a deep write, which writes the contents of
     * any containers encountered.
     */
    public abstract void writeIonEvents(IonReader reader) throws IOException;


    /**
     * Writes a value of Ion's null type ({@code null} or {@code null.null}).
     * @throws IOException
     */
    public abstract void writeNull() throws IOException;

    /**
     * writes a null value of the passed in type.
     * @param type type of the null to be written
     * @throws IOException
     */
    public abstract void writeNull(IonType type) throws IOException;


    /**
     * writes a non-null boolean value (true or false) as an IonBool
     * to output.
     * @param value true or false as desired
     * @throws IOException
     */
    public abstract void writeBool(boolean value) throws IOException;


    /**
     * writes a signed 8 bit value, a Java byte, as an IonInt.
     * @param value signed int to write
     * @throws IOException
     */
    public abstract void writeInt(byte value) throws IOException;

    /**
     * writes a signed 16 bit value, a Java short, as an IonInt.
     * @param value signed int to write
     * @throws IOException
     */
    public abstract void writeInt(short value) throws IOException;

    /**
     * writes a signed 32 bit value, a Java int, as an IonInt.
     * @param value signed int to write
     * @throws IOException
     */
    public abstract void writeInt(int value) throws IOException;

    /**
     * writes a signed 64 bit value, a Java byte, as an IonInt.
     * @param value signed int to write
     * @throws IOException
     */
    public abstract void writeInt(long value) throws IOException;

    // TODO add writeInt(BigInteger)


    /**
     * writes a 32 bit binary floaing point value, a Java float,
     * as an IonFloat.  Currently IonFloat values are output as
     * 64 bit IEEE 754 big endian values.  As a result writeFloat
     * is simply a convienience method which casts the float
     * up to a double on output.
     * @param value float to write
     * @throws IOException
     */
    public abstract void writeFloat(float value) throws IOException;

    /**
     * writes a 64 bit binary floaing point value, a Java double,
     * as an IonFloat.  Currently IonFloat values are output as
     * 64 bit IEEE 754 big endian values.  IonFloat preserves all
     * valid floating point values, including -0.0, Nan and +/-infinity.
     * It does not gaurantee preservation of -Nan or other less
     * less "common" values.
     * @param value double to write
     * @throws IOException
     */
    public abstract void writeFloat(double value) throws IOException;


    /**
     * writes a BigDecimal value as an IonDecimal.  Ion uses an
     * arbitrarily long sign/value and an arbitartily long signed
     * exponent to write the value. This preserves
     * all of the BigDecimal digits, the number of
     * significant digits and -0.0.
     * @param value BigDecimal to write
     * @throws IOException
     */
    public abstract void writeDecimal(BigDecimal value) throws IOException;


    /**
     *
     * @param value may be null to represent {@code null.timestamp}.
     */
    public abstract void writeTimestamp(TtTimestamp value) throws IOException;

    /**
     * writes the passed in Date (in milliseconds since the epoch) as an
     * IonTimestamp.  The Date value is treated as a UTC value with an
     * unknown timezone offset (a z value).
     * @param value java.util Date holding the UTC timestamp;
     * may be null to represent {@code null.timestamp}.
     * @throws IOException
     */
    public abstract void writeTimestampUTC(Date value) throws IOException;

    /**
     * writes the passed in Date (in milliseconds since the epoch) as an
     * IonTimestamp with the associated timezone offset.  The Date value
     * is treated as a UTC date and time value.  The offset is the offset
     * of the timezone where the value originated. (the date is expected
     * to have already be adjusted to UTC if necessary)
     * @param value java.util Date holding the UTC timestamp;
     * may be null to represent {@code null.timestamp}.
     * @param localOffset minutes from UTC where the value was authored
     * @throws IOException
     */
    public abstract void writeTimestamp(Date value, Integer localOffset)
        throws IOException;

    /**
     * write symbolId out as an IonSymbol value.  The value does not
     * have to be valid in the symbol table, unless the output is
     * text, in which case it does.
     * @param symbolId symbol table id to write
     * @throws IOException
     */
    public abstract void writeSymbol(int symbolId) throws IOException;

    /**
     * write value out as an IonSymbol value.  If the value is not
     * present in the symbol table it will be added to the symbol table.
     * @param value string symbol write
     * @throws IOException
     */
    public abstract void writeSymbol(String value) throws IOException;

    /**
     * writes the Java string value out as a IonString.  IonStrings are
     * UTF-8 and Java Strings are Unicode 16.  As such the resulting
     * lengths may not match.  In addition some Java strings are not
     * valid as they may contain only one of the two needed surrogate
     * charaters necesssary to define the Unicode code point to be
     * output, an exception will be raised if this case is encountered.
     * @param value Java String to be written
     * @throws IOException
     */
    public abstract void writeString(String value) throws IOException;

    /**
     * write the byte array out as an IonClob value.  This copies
     * the byte array.
     * @param value bytes to be written
     * @throws IOException
     */
    public abstract void writeClob(byte[] value) throws IOException;

    /**
     * writes a portion of the byte array out as an IonClob value.  This
     * copies the porition of the byte array that is written.
     * @param value bytes to be written
     * @param start offset of the first byte in value to write
     * @param len number of bytes to write from value
     * @throws IOException
     */
    public abstract void writeClob(byte[] value, int start, int len)
        throws IOException;

    /**
     * write the byte array out as an IonBlob value.  This copies
     * the byte array.
     * @param value bytes to be written
     * @throws IOException
     */
    public abstract void writeBlob(byte[] value) throws IOException;

    /**
     * writes a portion of the byte array out as an IonBlob value.  This
     * copies the porition of the byte array that is written.
     * @param value bytes to be written
     * @param start offset of the first byte in value to write
     * @param len number of bytes to write from value
     * @throws IOException
     */
    public abstract void writeBlob(byte[] value, int start, int len)
        throws IOException;

    /**
     * writes a struct header for and IonStruct and prepares to write
     * the structures fields.  This must be matched by a closeStruct()
     * at the end of the field list.
     * @throws IOException
     */
    public abstract void openStruct() throws IOException;

    /**
     * writes a list header for an IonList and prepares to write the
     * lists members.  This must be matched by a closeList() at the end
     * of the members.
     * @throws IOException
     */
    public abstract void openList() throws IOException;

    /**
     * writes a list header for an IonSexp and prepares to write the
     * lists members.  This must be matched by a closeList() at the end
     * of the members.
     * @throws IOException
     */
    public abstract void openSexp() throws IOException;

    /**
     * closes an IonStruct. This returns the writer to the
     * context of this structures parent container.
     * @throws IOException
     */
    public abstract void closeStruct() throws IOException;

    /**
     * closes an IonList. This returns the writer to the
     * context of this structures parent container.
     * @throws IOException
     */
    public abstract void closeList() throws IOException;

    /**
     * closes an IonSexp. This returns the writer to the
     * context of this structures parent container.
     * @throws IOException
     */
    public abstract void closeSexp() throws IOException;

    /**
     * helper function that can tell the caller whether
     * the writer is currently writing the contents of
     * an IonStruct.  This is especially useful when it
     * is not clear whether field names need to be
     * written or not.
     * @return boolean
     */
    public abstract boolean isInStruct();


    /**
     * writes an IonList with a series of IonBool values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values boolean values to populate the list with
     * @throws IOException
     */
    public abstract void writeBoolList(boolean[] values) throws IOException;

    /**
     * writes an IonList with a series of IonInt values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values signed byte values to populate the lists int's with
     * @throws IOException
     */
    public abstract void writeIntList(byte[] values) throws IOException;

    /**
     * writes an IonList with a series of IonInt values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values signed short values to populate the lists int's with
     * @throws IOException
     */
    public abstract void writeIntList(short[] values) throws IOException;

    /**
     * writes an IonList with a series of IonInt values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values signed int values to populate the lists int's with
     * @throws IOException
     */
    public abstract void writeIntList(int[] values) throws IOException;

    /**
     * writes an IonList with a series of IonInt values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values signed long values to populate the lists int's with
     * @throws IOException
     */
    public abstract void writeIntList(long[] values) throws IOException;

    /**
     * writes an IonList with a series of IonFloat values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.  Note that since, currently, IonFloat
     * is a 64 bit float this is a helper that simply casts
     * the passed in floats to double before writing them.
     * @param values 32 bit float values to populate the lists IonFloat's with
     * @throws IOException
     */
    public abstract void writeFloatList(float[] values) throws IOException;

    /**
     * writes an IonList with a series of IonFloat values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values 64 bit float values to populate the lists IonFloat's with
     * @throws IOException
     */
    public abstract void writeFloatList(double[] values) throws IOException;

    /**
     * writes an IonList with a series of IonString values. This
     * starts a List, writes the values (without any annoations)
     * and closes the list. For text and tree writers this is
     * just a convienience, but for the binary writer it can be
     * optimized internally.
     * @param values Java String to populate the lists IonString's from
     * @throws IOException
     */
    public abstract void writeStringList(String[] values) throws IOException;


    /**
     * returns the current contents of the write as a new byte
     * array.  This allocates an array of the size needed to exactly
     * hold the output and copies the entire value to it.  In the
     * case of the text writer this value has UTF-8 text in it. The
     * binary write fills it with the binary encoding of the values
     * and a local symbol table as well as a magic cookie.  The tree
     * writer defers to the tree to build this array.
     * @return the byte array with the writers output
     * @throws IOException
     */
    public abstract byte[] getBytes() throws IOException;

    /**
     * copies the current contents of the writer to the users byte
     * array.  This starts writing to the array at offset and writes
     * up to maxlen bytes.  In the
     * case of the text writer this value has UTF-8 text in it. The
     * binary write fills it with the binary encoding of the values
     * and a local symbol table as well as a magic cookie.  The tree
     * writer defers to the tree to build this array.  If the
     * underlying writer is not able to stop in the middle of its
     * work this may overwrite the array and later throw and exception.
     * @param bytes users byte array to write into
     * @param offset initial offset in the array to write into
     * @param maxlen maximum number of bytes to write
     * @return number of bytes written
     * @throws IOException
     */
    public abstract int    getBytes(byte[] bytes, int offset, int maxlen)
        throws IOException;

    /**
     * writes the contents of the writer into this output stream.
     * @param out OutputStream to write into
     * @return number of bytes written to the stream
     * @throws IOException
     */
//    public abstract int    writeBytes(SimpleByteBuffer.SimpleByteWriter out)
//        throws IOException;
}
