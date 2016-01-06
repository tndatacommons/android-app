package org.tndata.android.compass.util;

import android.content.Context;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.util.Log;

import org.xml.sax.XMLReader;

import java.util.Stack;


/**
 * The application's tag handler. Turns HTML into displayable text.
 *
 * @author Damian Urbanczyk
 * @author Edited, formatted, and documented by Ismael Alonso
 * @version 1.0.0
 */
public class CompassTagHandler implements TagHandler{
    private static final String TAG = "CompassTagHandler";


    //Keeps track of lists (ol, ul). At the bottom of the Stack is the outermost
    //  list and at the top of the Stack is the most nested list
    private Stack<String> lists = new Stack<>();

    //Tracks indices of ordered lists so that after a nested list ends we can
    //  continue with correct index of outer list
    private Stack<Integer> olNextIndex = new Stack<>();

    //List indentation in pixels. Nested lists use multiple of this
    private final int indent;
    private final int listItemIndent;
    private final BulletSpan bullet;


    /**
     * Constructor,
     *
     * @param context a reference to the context.
     */
    public CompassTagHandler(Context context){
        indent = CompassUtil.getPixels(context, 15);
        listItemIndent = indent * 2;
        bullet = new BulletSpan(indent);
    }

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader){
        if (tag.equalsIgnoreCase("ul")){
            if (opening){
                lists.push(tag);
            }
            else{
                lists.pop();
            }
        }
        else if (tag.equalsIgnoreCase("ol")){
            if (opening){
                lists.push(tag);
                //TODO add support for lists starting indices other than 1
                olNextIndex.push(1);
            }
            else{
                lists.pop();
                olNextIndex.pop();
            }
        }
        else if (tag.equalsIgnoreCase("li")){
            if (opening){
                if (output.length() > 0 && output.charAt(output.length() - 1) != '\n'){
                    output.append("\n");
                }
                String parentList = lists.peek();
                if (parentList.equalsIgnoreCase("ol")){
                    start(output, new Ol());
                    output.append(olNextIndex.peek().toString()).append(". ");
                    olNextIndex.push(olNextIndex.pop() + 1);
                }
                else if (parentList.equalsIgnoreCase("ul")){
                    start(output, new Ul());
                }
            }
            else{
                if (lists.peek().equalsIgnoreCase("ul")){
                    if (output.length() > 0 && output.charAt(output.length() - 1) != '\n'){
                        output.append("\n");
                    }
                    // Nested BulletSpans increases distance between bullet and text, so we must prevent it.
                    int bulletMargin = indent;
                    if (lists.size() > 1){
                        bulletMargin = indent - bullet.getLeadingMargin(true);
                        if (lists.size() > 2){
                            // This get's more complicated when we add a LeadingMarginSpan into the same line:
                            // we have also counter it's effect to BulletSpan
                            bulletMargin -= (lists.size() - 2) * listItemIndent;
                        }
                    }
                    BulletSpan newBullet = new BulletSpan(bulletMargin);
                    end(output,
                            Ul.class,
                            new LeadingMarginSpan.Standard(listItemIndent * (lists.size() - 1)),
                            newBullet);
                }
                else if (lists.peek().equalsIgnoreCase("ol")){
                    if (output.length() > 0 && output.charAt(output.length() - 1) != '\n'){
                        output.append("\n");
                    }
                    int numberMargin = listItemIndent * (lists.size() - 1);
                    if (lists.size() > 2){
                        // Same as in ordered lists: counter the effect of nested Spans
                        numberMargin -= (lists.size() - 2) * listItemIndent;
                    }
                    end(output, Ol.class, new LeadingMarginSpan.Standard(numberMargin));
                }
            }
        }
        else{
            if (opening){
                Log.d(TAG, "Found an unsupported tag: " + tag);
            }
        }
    }

    /**
     * @see android.text.Html
     */
    private static void start(Editable text, Object mark){
        int len = text.length();
        text.setSpan(mark, len, len, Spanned.SPAN_MARK_MARK);
    }

    /**
     * Modified from {@link android.text.Html}
     */
    private static void end(Editable text, Class<?> kind, Object... replaces){
        int len = text.length();
        Object obj = getLast(text, kind);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        if (where != len){
            for (Object replace : replaces){
                text.setSpan(replace, where, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * @see android.text.Html
     */
    private static Object getLast(Spanned text, Class<?> kind){
        /*
		 * This knows that the last returned object from getSpans()
		 * will be the most recently added.
		 */
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0){
            return null;
        }
        return objs[objs.length - 1];
    }


    private static class Ul{

    }


    private static class Ol{

    }
}
