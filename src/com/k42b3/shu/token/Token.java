/**
 * shu
 * An application wich parses an PHP project and extracts all class, method and 
 * function definitions. Based on the definition it can build various charts
 * and tables to given an overview of the project
 * 
 * Copyright (c) 2013 Christoph Kappestein <k42b3.x@gmail.com>
 * 
 * This file is part of shu. shu is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, 
 * either version 3 of the License, or at any later version.
 * 
 * shu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with shu. If not, see <http://www.gnu.org/licenses/>.
 */

package com.k42b3.shu.token;

/**
 * Token
 *
 * @author  Christoph Kappestein <k42b3.x@gmail.com>
 * @license http://www.gnu.org/licenses/gpl.html GPLv3
 * @link    https://github.com/k42b3/shu
 */
public class Token
{
	public static final int T_REQUIRE_ONCE = 258;
	public static final int T_REQUIRE = 259;
	public static final int T_EVAL = 260;
	public static final int T_INCLUDE_ONCE = 261;
	public static final int T_INCLUDE = 262;
	public static final int T_LOGICAL_OR = 263;
	public static final int T_LOGICAL_XOR = 264;
	public static final int T_LOGICAL_AND = 265;
	public static final int T_PRINT = 266;
	public static final int T_SR_EQUAL = 267;
	public static final int T_SL_EQUAL = 268;
	public static final int T_XOR_EQUAL = 269;
	public static final int T_OR_EQUAL = 270;
	public static final int T_AND_EQUAL = 271;
	public static final int T_MOD_EQUAL = 272;
	public static final int T_CONCAT_EQUAL = 273;
	public static final int T_DIV_EQUAL = 274;
	public static final int T_MUL_EQUAL = 275;
	public static final int T_MINUS_EQUAL = 276;
	public static final int T_PLUS_EQUAL = 277;
	public static final int T_BOOLEAN_OR = 278;
	public static final int T_BOOLEAN_AND = 279;
	public static final int T_IS_NOT_IDENTICAL = 280;
	public static final int T_IS_IDENTICAL = 281;
	public static final int T_IS_NOT_EQUAL = 282;
	public static final int T_IS_EQUAL = 283;
	public static final int T_IS_GREATER_OR_EQUAL = 284;
	public static final int T_IS_SMALLER_OR_EQUAL = 285;
	public static final int T_SR = 286;
	public static final int T_SL = 287;
	public static final int T_INSTANCEOF = 288;
	public static final int T_UNSET_CAST = 289;
	public static final int T_BOOL_CAST = 290;
	public static final int T_OBJECT_CAST = 291;
	public static final int T_ARRAY_CAST = 292;
	public static final int T_STRING_CAST = 293;
	public static final int T_DOUBLE_CAST = 294;
	public static final int T_INT_CAST = 295;
	public static final int T_DEC = 296;
	public static final int T_INC = 297;
	public static final int T_CLONE = 298;
	public static final int T_NEW = 299;
	public static final int T_EXIT = 300;
	public static final int T_IF = 301;
	public static final int T_ELSEIF = 302;
	public static final int T_ELSE = 303;
	public static final int T_ENDIF = 304;
	public static final int T_LNUMBER = 305;
	public static final int T_DNUMBER = 306;
	public static final int T_STRING = 307;
	public static final int T_STRING_VARNAME = 308;
	public static final int T_VARIABLE = 309;
	public static final int T_NUM_STRING = 310;
	public static final int T_INLINE_HTML = 311;
	public static final int T_CHARACTER = 312;
	public static final int T_BAD_CHARACTER = 313;
	public static final int T_ENCAPSED_AND_WHITESPACE = 314;
	public static final int T_CONSTANT_ENCAPSED_STRING = 315;
	public static final int T_ECHO = 316;
	public static final int T_DO = 317;
	public static final int T_WHILE = 318;
	public static final int T_ENDWHILE = 319;
	public static final int T_FOR = 320;
	public static final int T_ENDFOR = 321;
	public static final int T_FOREACH = 322;
	public static final int T_ENDFOREACH = 323;
	public static final int T_DECLARE = 324;
	public static final int T_ENDDECLARE = 325;
	public static final int T_AS = 326;
	public static final int T_SWITCH = 327;
	public static final int T_ENDSWITCH = 328;
	public static final int T_CASE = 329;
	public static final int T_DEFAULT = 330;
	public static final int T_BREAK = 331;
	public static final int T_CONTINUE = 332;
	public static final int T_GOTO = 333;
	public static final int T_FUNCTION = 334;
	public static final int T_CONST = 335;
	public static final int T_RETURN = 336;
	public static final int T_TRY = 337;
	public static final int T_CATCH = 338;
	public static final int T_THROW = 339;
	public static final int T_USE = 340;
	public static final int T_INSTEADOF = 341;
	public static final int T_GLOBAL = 342;
	public static final int T_PUBLIC = 343;
	public static final int T_PROTECTED = 344;
	public static final int T_PRIVATE = 345;
	public static final int T_FINAL = 346;
	public static final int T_ABSTRACT = 347;
	public static final int T_STATIC = 348;
	public static final int T_VAR = 349;
	public static final int T_UNSET = 350;
	public static final int T_ISSET = 351;
	public static final int T_EMPTY = 352;
	public static final int T_HALT_COMPILER = 353;
	public static final int T_CLASS = 354;
	public static final int T_TRAIT = 355;
	public static final int T_INTERFACE = 356;
	public static final int T_EXTENDS = 357;
	public static final int T_IMPLEMENTS = 358;
	public static final int T_OBJECT_OPERATOR = 359;
	public static final int T_DOUBLE_ARROW = 360;
	public static final int T_LIST = 361;
	public static final int T_ARRAY = 362;
	public static final int T_CALLABLE = 363;
	public static final int T_CLASS_C = 364;
	public static final int T_TRAIT_C = 365;
	public static final int T_METHOD_C = 366;
	public static final int T_FUNC_C = 367;
	public static final int T_LINE = 368;
	public static final int T_FILE = 369;
	public static final int T_COMMENT = 370;
	public static final int T_DOC_COMMENT = 371;
	public static final int T_OPEN_TAG = 372;
	public static final int T_OPEN_TAG_WITH_ECHO = 373;
	public static final int T_CLOSE_TAG = 374;
	public static final int T_WHITESPACE = 375;
	public static final int T_START_HEREDOC = 376;
	public static final int T_END_HEREDOC = 377;
	public static final int T_DOLLAR_OPEN_CURLY_BRACES = 378;
	public static final int T_CURLY_OPEN = 379;
	public static final int T_PAAMAYIM_NEKUDOTAYIM = 380;
	public static final int T_NAMESPACE = 381;
	public static final int T_NS_C = 382;
	public static final int T_DIR = 383;
	public static final int T_NS_SEPARATOR = 384;
	public static final int T_DOUBLE_COLON = 380;

	protected int type;
	protected String value;
	protected int line;

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public int getLine()
	{
		return line;
	}

	public void setLine(int line)
	{
		this.line = line;
	}
	
	public String toString()
	{
		return "" + this.getType();
	}
}
